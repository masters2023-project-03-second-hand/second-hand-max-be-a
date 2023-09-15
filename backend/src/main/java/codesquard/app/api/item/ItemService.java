package codesquard.app.api.item;

import java.util.List;

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
import codesquard.app.api.response.ItemResponse;
import codesquard.app.api.response.ItemResponses;
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
@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;
	private final CategoryRepository categoryRepository;
	private final ItemPaginationRepository itemPaginationRepository;
	private final ItemValidator itemValidator;

	@Transactional
	public void register(ItemRegisterRequest request, List<MultipartFile> itemImage, Long memberId) {
		List<String> serverFileUrls = imageService.uploadImages(itemImage);
		Member writer = new Member(memberId);
		String thumbnailUrl = serverFileUrls.get(0);
		Item item = request.toEntity(writer, thumbnailUrl);
		Item saveItem = itemRepository.save(item);

		List<Image> images = Image.create(serverFileUrls, saveItem);
		imageRepository.saveAll(images);
	}

	@Transactional(readOnly = true)
	public ItemResponses findAll(String region, int size, Long cursor, Long categoryId) {
		Slice<ItemResponse> itemResponses = itemPaginationRepository.findByIdAndRegion(cursor, region, size,
			categoryId);
		return PaginationUtils.getItemResponses(itemResponses);
	}

	@Transactional
	public void modifyItem(Long itemId, ItemModifyRequest request, List<MultipartFile> addImages, Principal writer) {
		log.info("상품 수정 서비스 요청 : itemId={}, request={}, writer={}", itemId, request, writer.getLoginId());
		Long changeCategoryId = request.getCategoryId();
		List<String> deleteImageUrls = request.getDeleteImageUrls();

		itemValidator.validateContainsImage(deleteImageUrls, itemId);

		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		log.debug("상품 수정 서비스의 상품 조회 결과 : {}", item);

		item.validateAuthorization(writer);

		Category category = categoryRepository.findById(changeCategoryId)
			.orElseThrow(() -> new RestApiException(CategoryErrorCode.NOT_FOUND_CATEGORY));
		log.debug("상품 수정 서비스의 카테고리 조회 결과 : {}", category);

		List<String> addImageUrls = imageService.uploadImages(addImages);
		log.debug("상품 수정 서비스의 S3 이미지 추가 결과 : {}", addImageUrls);

		List<Image> images = Image.create(addImageUrls, item);
		List<Image> saveImages = imageRepository.saveAll(images);
		log.debug("상품 수정 서비스의 이미지 테이블 저장 결과 : {}", saveImages);

		int deleteImageSize = deleteImagesFromRepository(itemId, deleteImageUrls);
		log.debug("이미지 테이블의 삭제 결과 : 삭제 개수={}", deleteImageSize);

		deleteImagesFromS3(deleteImageUrls);

		String thumbnailUrl = findThumbnailUrlBy(item);
		Item changeItem = request.toEntity(thumbnailUrl);
		item.changeCategory(category);
		item.changeBy(changeItem);
	}

	private int deleteImagesFromRepository(Long itemId, List<String> deleteImageUrls) {
		int currentImageSize = imageRepository.countImageByItemId(itemId);
		if (currentImageSize <= deleteImageUrls.size()) {
			throw new RestApiException(ImageErrorCode.NOT_REMOVE_IMAGES);
		}
		return imageRepository.deleteImagesByImageUrlIn(deleteImageUrls);
	}

	private String findThumbnailUrlBy(Item item) {
		List<Image> images = imageRepository.findAllByItemId(item.getId());
		if (images.isEmpty()) {
			throw new RestApiException(ImageErrorCode.EMPTY_IMAGE);
		}
		return images.get(0).getImageUrl();
	}

	private void deleteImagesFromS3(List<String> deleteImageUrls) {
		deleteImageUrls.forEach(imageService::deleteImage);
	}

	@Transactional(readOnly = true)
	public void findById(Long itemId, ItemStatus status) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new RestApiException(ItemErrorCode.ITEM_NOT_FOUND));
		item.changeStatus(status);
	}
}
