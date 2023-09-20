package codesquard.app.api.item;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.errors.errorcode.CategoryErrorCode;
import codesquard.app.api.errors.errorcode.ImageErrorCode;
import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.image.ImageService;
import codesquard.app.api.item.request.ItemModifyRequest;
import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.category.CategoryRepository;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemPaginationRepository;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;
	private final CategoryRepository categoryRepository;
	private final ItemPaginationRepository itemPaginationRepository;

	@Transactional
	public void register(ItemRegisterRequest request, List<MultipartFile> itemImage, Long memberId) {
		List<String> serverFileUrls = imageService.uploadImages(itemImage);
		Member writer = new Member(memberId);
		String thumbnailUrl = serverFileUrls.get(0);
		Item item = request.toEntity(writer, thumbnailUrl);
		Item saveItem = itemRepository.save(item);

		List<Image> images = Image.createImages(serverFileUrls, saveItem);
		imageRepository.saveAll(images);
	}

	public ItemResponses findAll(String region, int size, Long cursor, Long categoryId) {
		Slice<ItemResponse> itemResponses = itemPaginationRepository.findByIdAndRegion(cursor, region, size,
			categoryId);
		return PaginationUtils.getItemResponses(itemResponses);
	}

	public void findById(Long itemId, ItemStatus status) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		item.changeStatus(status);
	}

	public ItemDetailResponse findDetailItemBy(Long itemId, Long loginMemberId) {
		log.info("상품 상세 조회 서비스 요청, 상품 등록번호 : {}, 로그인 회원의 등록번호 : {}", itemId, loginMemberId);
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		List<String> imageUrls = mapToImageUrls(item);
		Member seller = item.getMember();
		return ItemDetailResponse.of(item, seller, loginMemberId, imageUrls);
	}

	private List<String> mapToImageUrls(Item item) {
		List<Image> images = imageRepository.findAllByItemId(item.getId());
		return images.stream()
			.map(Image::getImageUrl)
			.collect(Collectors.toUnmodifiableList());
	}

	@Transactional
	public void modifyItem(Long itemId, ItemModifyRequest request, List<MultipartFile> addImages,
		MultipartFile thumnailFile, Principal writer) {
		log.info("상품 수정 서비스 요청 : itemId={}, request={}, writer={}", itemId, request, writer.getLoginId());

		Item item = findItemBy(itemId);
		log.debug("상품 수정 서비스의 상품 조회 결과 : {}", item);
		item.validateSeller(writer.getMemberId());

		List<String> addImageUrls = imageService.uploadImages(addImages);
		log.debug("상품 수정 서비스의 S3 이미지 추가 결과 : {}", addImageUrls);

		List<Image> saveImages = imageRepository.saveAll(Image.createImages(addImageUrls, new Item(itemId)));
		log.debug("상품 수정 서비스의 이미지 테이블 저장 결과 : {}", saveImages);

		List<String> deleteImageUrls = request.getDeleteImageUrls();
		int deleteImageSize = deleteImages(itemId, deleteImageUrls);
		log.debug("이미지 테이블의 삭제 결과 : 삭제 개수={}", deleteImageSize);

		deleteImagesFromS3(deleteImageUrls);

		Category category = findCategoryBy(request.getCategoryId());
		log.debug("상품 수정 서비스의 카테고리 조회 결과 : {}", category);

		String thumbnailUrl = updateThumnail(item, thumnailFile, request.getThumnailImage());
		log.debug("썸네일 갱신 결과 : thumbnailUrl={}", thumbnailUrl);

		item.change(category, request.toEntity(), thumbnailUrl);
	}

	private Category findCategoryBy(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new RestApiException(CategoryErrorCode.NOT_FOUND_CATEGORY));
	}

	private String updateThumnail(Item item, MultipartFile thumnailFile, String thumnailUrl) {
		if (thumnailFile != null) {
			return updateNewThumnail(item.getId(), thumnailFile);
		}
		if (thumnailUrl != null) {
			return updateExistThumnail(thumnailUrl, item);
		}
		return item.getThumbnailUrl();
	}

	private String updateNewThumnail(Long itemId, MultipartFile thumnailFile) {
		String thumnailImageUrl = imageService.uploadImage(thumnailFile);
		log.debug("썸네일 이미지 S3 업로드 결과 : thumnailImageUrl={}", thumnailImageUrl);

		Image thumnail = imageRepository.save(Image.thumnail(thumnailImageUrl, itemId));
		log.debug("썸네일 이미지 테이블 저장 결과 : image={}", thumnail);
		return thumnailImageUrl;
	}

	private String updateExistThumnail(String changeThumnail, Item item) {
		if (changeThumnail == null) {
			return null;
		}
		if (!item.equalThumnailImageUrl(changeThumnail)) {
			return null;
		}

		int result = imageRepository.updateThumnailByItemIdAndImageUrl(item.getId(), item.getThumbnailUrl(), false);
		log.debug("기존 이미지 썸네일 표시 변경 결과 : result={}", result);

		result = imageRepository.updateThumnailByItemIdAndImageUrl(item.getId(), changeThumnail, true);
		log.debug("요청 이미지 썸네일 표시 변경 결과 : result={}", result);

		return changeThumnail;
	}

	private int deleteImages(Long itemId, List<String> deleteImageUrls) {
		int currentImageSize = imageRepository.countImageByItemId(itemId);
		if (currentImageSize <= deleteImageUrls.size()) {
			throw new RestApiException(ImageErrorCode.NOT_REMOVE_IMAGES);
		}
		return imageRepository.deleteImagesByItemIdAndImageUrlIn(itemId, deleteImageUrls);
	}

	private void deleteImagesFromS3(List<String> deleteImageUrls) {
		deleteImageUrls.forEach(imageService::deleteImage);
	}

	private Item findItemBy(Long itemId) {
		return itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
	}
}
