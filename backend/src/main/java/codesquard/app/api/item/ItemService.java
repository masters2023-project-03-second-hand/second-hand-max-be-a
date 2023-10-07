package codesquard.app.api.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.errors.errorcode.CategoryErrorCode;
import codesquard.app.api.errors.errorcode.ImageErrorCode;
import codesquard.app.api.errors.errorcode.ItemErrorCode;
import codesquard.app.api.errors.exception.BadRequestException;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.api.image.ImageService;
import codesquard.app.api.item.request.ItemModifyRequest;
import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.api.item.response.ItemDetailResponse;
import codesquard.app.api.item.response.ItemResponse;
import codesquard.app.api.item.response.ItemResponses;
import codesquard.app.api.redis.ItemViewRedisService;
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
import codesquard.app.domain.wish.Wish;
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
	private final ItemViewRedisService itemViewRedisService;

	@Transactional
	public void register(ItemRegisterRequest request, List<MultipartFile> itemImages,
		MultipartFile thumbnail, Long memberId) {
		log.info("상품 등록 서비스 요청 : request={}", request);

		String thumbnailUrl = imageService.uploadImage(thumbnail);
		log.info("S3 썸네일 저장 결과 URL : {}", thumbnailUrl);

		Member writer = new Member(memberId);
		Item item = itemRepository.save(request.toEntity(writer, thumbnailUrl));
		log.info("상품 저장 결과 : {}", item);

		Image saveThumbnailUrl = imageRepository.save(Image.thumbnail(thumbnailUrl, item.getId()));
		log.info("썸네일 저장 결과 : {}", saveThumbnailUrl);

		if (itemImages != null) {
			List<String> serverFileUrls = imageService.uploadImages(itemImages);
			log.info("S3 일반 이미지 저장 결과 URL : {}", serverFileUrls);

			List<Image> images = imageRepository.saveAll(Image.createImages(serverFileUrls, item));
			log.info("일반 이미지 저장 결과 : {}", images);
		}
	}

	public ItemResponses findAll(String region, int size, Long cursor, Long categoryId) {
		log.info("상품 목록 조회 서비스 요청 : region={}, size={}, cursor={}, categoryId={}", region, size, cursor, categoryId);
		Slice<ItemResponse> itemResponses = itemPaginationRepository.findByIdAndRegion(cursor, region, size,
			categoryId);
		return PaginationUtils.getItemResponses(itemResponses);
	}

	@Transactional
	public void changeItemStatus(Long itemId, ItemStatus status, Principal principal) {
		log.info("상품 상태 변경 서비스 요청 : itemId={}, status={}", itemId, status);
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NotFoundResourceException(ItemErrorCode.ITEM_NOT_FOUND));
		if (item.isSeller(principal.getMemberId())) {
			item.changeStatus(status);
			log.info("상품 상태 변경 결과 : item={}", item);
		} else {
			new BadRequestException(ItemErrorCode.ITEM_FORBIDDEN);
		}
	}

	public ItemDetailResponse findDetailItemBy(Long itemId, Principal principal) {
		log.info("상품 상세 조회 서비스 요청, 상품 등록번호 : {}, 로그인 회원의 등록번호 : {}", itemId, principal.getMemberId());
		itemViewRedisService.addViewCount(itemId, principal);
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NotFoundResourceException(ItemErrorCode.ITEM_NOT_FOUND));

		List<Image> images = imageRepository.findAllByItemId(itemId);
		List<String> imageUrls = images.stream()
			.map(Image::getImageUrl)
			.collect(Collectors.toUnmodifiableList());

		boolean isInWishList = wishRepository.existsByMemberIdAndItemId(principal.getMemberId(), itemId);

		if (item.isSeller(principal.getMemberId())) {
			return ItemDetailResponse.toSeller(item, principal.getMemberId(), imageUrls, isInWishList);
		}
		Long chatRoomId = chatRoomRepository.findByItemIdAndMemberId(itemId, principal.getMemberId())
			.orElse(null);
		return ItemDetailResponse.toBuyer(item, principal.getMemberId(), imageUrls, isInWishList, chatRoomId);
	}

	@Transactional
	public void modifyItem(Long itemId, ItemModifyRequest request, List<MultipartFile> addImages,
		MultipartFile thumbnailFile, Principal writer) {
		log.info("상품 수정 서비스 요청 : itemId={}, request={}, writer={}", itemId, request, writer.getLoginId());
		log.info("상품 수정 서비스 요청 : addImages={}, thumnailFile={}", addImages, thumbnailFile);

		Item item = findItemByItemIdAndMemberId(itemId, writer.getMemberId());
		log.info("상품 수정 서비스의 상품 조회 결과 : {}", item);

		List<String> addImageUrls = imageService.uploadImages(addImages);
		log.info("상품 수정 서비스의 S3 이미지 추가 결과 : {}", addImageUrls);

		List<Image> saveImages = imageRepository.saveAll(Image.createImages(addImageUrls, new Item(itemId)));
		log.info("상품 수정 서비스의 이미지 테이블 저장 결과 : {}", saveImages);

		List<String> deleteImageUrls = request.getDeleteImageUrls();

		int deleteImageSize = deleteImages(itemId, deleteImageUrls);
		log.info("이미지 테이블의 삭제 결과 : 삭제 개수={}", deleteImageSize);

		deleteImagesFromS3(deleteImageUrls);

		Category category = findCategoryBy(request.getCategoryId());

		String thumbnailUrl = updateThumnail(item, thumbnailFile, request.getThumnailImage());

		item.change(category, request.toEntity(), thumbnailUrl);
		log.info("상품 수정 결과 : {}", item);
	}

	private Category findCategoryBy(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new NotFoundResourceException(CategoryErrorCode.NOT_FOUND_CATEGORY));
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
		log.info("썸네일 이미지 S3 업로드 결과 : thumnailImageUrl={}", thumnailImageUrl);

		Image thumbnail = imageRepository.save(Image.thumbnail(thumnailImageUrl, itemId));
		log.info("썸네일 이미지 테이블 저장 결과 : image={}", thumbnail);
		return thumnailImageUrl;
	}

	private String updateThumbnailStatus(String changeThumbnail, Item item) {
		Optional.ofNullable(changeThumbnail).ifPresent(thumbnail -> {
			int result = imageRepository.updateThumnailToFalseByItemIdAndThumbnailIsTrue(item.getId());
			log.info("기존 이미지 썸네일 표시 변경 결과 : result={}", result);

			result = imageRepository.updateThumbnailByItemIdAndImageUrl(item.getId(), thumbnail, true);
			log.info("요청 이미지 썸네일 표시 변경 결과 : result={}", result);
		});
		return changeThumbnail;
	}

	private int deleteImages(Long itemId, List<String> deleteImageUrls) {
		int currentImageSize = imageRepository.countImageByItemId(itemId);
		if (currentImageSize <= deleteImageUrls.size()) {
			throw new BadRequestException(ImageErrorCode.NOT_REMOVE_IMAGES);
		}
		return imageRepository.deleteImagesByItemIdAndImageUrlIn(itemId, deleteImageUrls);
	}

	private void deleteImagesFromS3(List<String> deleteImageUrls) {
		deleteImageUrls.forEach(imageService::deleteImage);
	}

	private Item findItemByItemIdAndMemberId(Long itemId, Long memberId) {
		return itemRepository.findItemByIdAndMemberId(itemId, memberId)
			.orElseThrow(() -> new NotFoundResourceException(ItemErrorCode.ITEM_NOT_FOUND));
	}

	@Transactional
	public void deleteItem(Long itemId, Principal writer) {
		log.info("상품 게시글 삭제 서비스 요청 : itemId={}, writer={}", itemId, writer);
		Item item = findItemByItemIdAndMemberId(itemId, writer.getMemberId());
		List<Image> images = imageRepository.findAllByItemId(item.getId());
		images.stream()
			.map(Image::getImageUrl)
			.forEach(imageService::deleteImage);

		deleteAllRelatedItem(itemId);
	}

	private void deleteAllRelatedItem(Long itemId) {
		imageRepository.deleteByItemId(itemId);
		List<Long> wishIds = wishRepository.findByItemId(itemId).stream()
			.map(Wish::getId)
			.collect(Collectors.toUnmodifiableList());
		wishRepository.deleteAllByIdIn(wishIds);
		chatRoomRepository.deleteByItemId(itemId);
		itemRepository.deleteById(itemId);
	}
}
