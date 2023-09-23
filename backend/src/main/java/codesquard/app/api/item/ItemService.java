package codesquard.app.api.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
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
import codesquard.app.domain.chat.ChatRoomRepository;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.image.ImageRepository;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemPaginationRepository;
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.pagination.PaginationUtils;
import codesquard.app.domain.wish.WishRepository;
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
	private final WishRepository wishRepository;
	private final ChatRoomRepository chatRoomRepository;

	@Transactional
	public void register(ItemRegisterRequest request, List<MultipartFile> itemImages,
		MultipartFile thumbnail, Long memberId) {
		String thumbnailUrl = imageService.uploadImage(thumbnail);
		Member writer = new Member(memberId);
		Item saveItem = itemRepository.save(request.toEntity(writer, thumbnailUrl));
		List<String> serverFileUrls = imageService.uploadImages(itemImages);
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

	@Cacheable
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
		MultipartFile thumbnailFile, Principal writer) {
		log.info("상품 수정 서비스 요청 : itemId={}, request={}, writer={}", itemId, request, writer.getLoginId());
		log.info("상품 수정 서비스 요청 : addImages={}, thumnailFile={}", addImages, thumbnailFile);

		Item item = findItemByItemIdAndMemberId(itemId, writer.getMemberId());
		log.debug("상품 수정 서비스의 상품 조회 결과 : {}", item);

		List<String> addImageUrls = imageService.uploadImages(addImages);
		log.debug("상품 수정 서비스의 S3 이미지 추가 결과 : {}", addImageUrls);

		List<Image> saveImages = imageRepository.saveAll(Image.createImages(addImageUrls, new Item(itemId)));
		log.debug("상품 수정 서비스의 이미지 테이블 저장 결과 : {}", saveImages);

		List<String> deleteImageUrls = request.getDeleteImageUrls();
		log.debug("상품 수정 서비스시 이미지 삭제 URL 리스트 : deleteImageUrls={}", deleteImageUrls);

		int deleteImageSize = deleteImages(itemId, deleteImageUrls);
		log.debug("이미지 테이블의 삭제 결과 : 삭제 개수={}", deleteImageSize);

		deleteImagesFromS3(deleteImageUrls);

		Category category = findCategoryBy(request.getCategoryId());
		log.debug("상품 수정 서비스의 카테고리 조회 결과 : {}", category);

		String thumbnailUrl = updateThumnail(item, thumbnailFile, request.getThumnailImage());
		log.debug("썸네일 갱신 결과 : thumbnailUrl={}", thumbnailUrl);

		item.change(category, request.toEntity(), thumbnailUrl);
	}

	private Category findCategoryBy(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new RestApiException(CategoryErrorCode.NOT_FOUND_CATEGORY));
	}

	private String updateThumnail(Item item, MultipartFile thumbnailFile, String thumbnailUrl) {
		if (thumbnailFile == null) {
			return item.getThumbnailUrl();
		}
		if (!thumbnailFile.isEmpty()) {
			String thumbnail = updateNewThumnail(item.getId(), thumbnailFile);
			return updateThumbnailStatus(thumbnail, item);
		}
		return updateThumbnailStatus(thumbnailUrl, item);
	}

	private String updateNewThumnail(Long itemId, MultipartFile thumbnailFile) {
		String thumnailImageUrl = imageService.uploadImage(thumbnailFile);
		log.debug("썸네일 이미지 S3 업로드 결과 : thumnailImageUrl={}", thumnailImageUrl);

		Image thumbnail = imageRepository.save(Image.thumbnail(thumnailImageUrl, itemId));
		log.debug("썸네일 이미지 테이블 저장 결과 : image={}", thumbnail);
		return thumnailImageUrl;
	}

	private String updateThumbnailStatus(String changeThumbnail, Item item) {
		Optional.ofNullable(changeThumbnail).ifPresent(thumbnail -> {
			int result = imageRepository.updateThumnailToFalseByItemIdAndThumbnailIsTrue(item.getId());
			log.debug("기존 이미지 썸네일 표시 변경 결과 : result={}", result);

			result = imageRepository.updateThumbnailByItemIdAndImageUrl(item.getId(), thumbnail, true);
			log.debug("요청 이미지 썸네일 표시 변경 결과 : result={}", result);
		});
		return changeThumbnail;
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

	private Item findItemByItemIdAndMemberId(Long itemId, Long memberId) {
		return itemRepository.findItemByIdAndMemberId(itemId, memberId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
	}

	@Transactional
	public void deleteItem(Long itemId, Principal writer) {
		Item item = findItemByItemIdAndMemberId(itemId, writer.getMemberId());
		List<Image> images = imageRepository.findAllByItemId(item.getId());
		images.stream()
			.map(Image::getImageUrl)
			.forEach(imageService::deleteImage);

		deleteAllRelatedItem(itemId);
	}

	private void deleteAllRelatedItem(Long itemId) {
		imageRepository.deleteByItemId(itemId);
		wishRepository.deleteByItemId(itemId);
		chatRoomRepository.deleteByItemId(itemId);
		itemRepository.deleteById(itemId);
	}
}
