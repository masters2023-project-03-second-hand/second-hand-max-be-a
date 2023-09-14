package codesquard.app.api.item;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.category.CategoryFixedFactory;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.image.ImageUploader;
import codesquard.app.api.item.request.ItemModifyRequest;
import codesquard.app.api.item.request.ItemRegisterRequest;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.api.response.ItemResponse;
import codesquard.app.api.response.ItemResponses;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.image.Image;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.support.SupportRepository;

class ItemServiceTest extends IntegrationTestSupport {

	@Autowired
	private SupportRepository supportRepository;
	@MockBean
	private ImageUploader imageUploader;

	@Test
	@DisplayName("새로운 상품 등록에 성공한다.")
	void registerTest() {
		// given
		given(imageUploader.uploadImageToS3(any(), anyString())).willReturn("url");
		Category category = supportRepository.save(Category.create("식품", "~~~~"));
		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "pieeeeeee"));

		List<MultipartFile> multipartFiles = getMultipartFiles();
		ItemRegisterRequest request = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", ItemStatus.ON_SALE, category.getId(), null);

		// when
		itemService.register(request, multipartFiles, member.getId());
		Item item = supportRepository.findAll(Item.class).get(0);

		// then
		assertThat(item.getTitle()).isEqualTo(request.getTitle());
	}

	private List<MultipartFile> getMultipartFiles() {
		List<MultipartFile> mockMultipartFiles = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			mockMultipartFiles.add(new MockMultipartFile(
				"test-image",
				"test-image.png",
				MediaType.IMAGE_PNG_VALUE,
				"image-content".getBytes(StandardCharsets.UTF_8)));
		}
		return mockMultipartFiles;
	}

	@Test
	@DisplayName("상품 목록 조회에 성공한다.")
	void findAll() {
		// given
		Category category = supportRepository.save(Category.create("식품", "~~~~"));
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", ItemStatus.ON_SALE, category.getId(), null);
		ItemRegisterRequest request2 = new ItemRegisterRequest(
			"전기밥솥", null, null, "가양 1동", ItemStatus.ON_SALE, category.getId(), null);
		ItemRegisterRequest request3 = new ItemRegisterRequest(
			"노트북", null, null, "가양 1동", ItemStatus.ON_SALE, category.getId(), null);

		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "pieeeeeee"));
		supportRepository.save(request1.toEntity(member, "thumbnail"));
		Item item = supportRepository.save(request2.toEntity(member, "thumbnail"));
		supportRepository.save(request3.toEntity(member, "thumbnail"));

		// when
		ItemResponses all = itemService.findAll("가양 1동", 2, null, null);

		// then
		List<ItemResponse> contents = all.getContents();
		assertAll(
			() -> assertThat(contents.size()).isEqualTo(2),
			() -> assertThat(contents.get(0).getTitle()).isEqualTo("노트북"),
			() -> assertThat(all.getPaging().isHasNext()).isTrue(),
			() -> assertThat(all.getPaging().getNextCursor()).isEqualTo(item.getId()));
	}

	@DisplayName("회원은 상품의 정보를 수정한다")
	@Test
	public void modifyItem() throws IOException {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		MemberTown memberTown = MemberTown.create(getRegion("서울 송파구 가락동"), member);
		Category category = CategoryFixedFactory.createdFixedCategory();
		Item item = ItemFixedFactory.createFixedItem(member, category, 0L);
		List<Image> images = ImageFixedFactory.createFixedImages(item);

		categoryRepository.save(category);
		memberRepository.save(member);
		Principal principal = Principal.from(member);

		memberTownRepository.save(memberTown);
		Item saveItem = itemRepository.save(item);
		List<Image> saveImages = imageRepository.saveAll(images);

		List<MultipartFile> addImages = ImageFixedFactory.createFixedMultipartFile();
		ItemModifyRequest request = ItemFixedFactory.createFixedItemModifyRequest(category, saveImages);

		BDDMockito.given(imageUploader.uploadImageToS3(any(), any()))
			.willReturn("http://s3_image1.com", "http://s3_image2.com");
		// when
		itemService.modifyItem(saveItem.getId(), request, addImages, principal);
		// then
		Item modifiedItem = itemRepository.findById(saveItem.getId()).orElseThrow();
		assertAll(() -> {
			assertThat(modifiedItem)
				.extracting("title", "content", "price", "status", "region")
				.contains(request.getTitle(), request.getContent(), request.getPrice(), request.getStatus(),
					request.getRegion());
		});

		verifyItemImageSize(saveItem, 2);
	}

	private void verifyItemImageSize(Item item, int expeted) {
		List<Image> images = imageRepository.findAllByItemId(item.getId());
		assertThat(images).hasSize(expeted);
	}

	@DisplayName("회원은 상품을 수정할때 상품 이미지에 존재하지 않는 URL을 이용하여 이미지를 제거할 수 없다")
	@Test
	public void modifyItemWithNotExistDeleteUrls() throws IOException {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		MemberTown memberTown = MemberTown.create(getRegion("서울 송파구 가락동"), member);
		Category category = CategoryFixedFactory.createdFixedCategory();
		Item item = ItemFixedFactory.createFixedItem(member, category, 0L);
		List<Image> images = ImageFixedFactory.createFixedImages(item);
		List<MultipartFile> addImages = ImageFixedFactory.createFixedMultipartFile();

		categoryRepository.save(category);
		Member saveMember = memberRepository.save(member);
		Principal principal = Principal.from(saveMember);

		memberTownRepository.save(memberTown);
		Item saveItem = itemRepository.save(item);
		imageRepository.saveAll(images);

		List<Image> deleteImages = List.of(Image.create("http://invalidurl.com", item));
		ItemModifyRequest request = ItemFixedFactory.createFixedItemModifyRequest(category, deleteImages);
		// when
		Throwable throwable = catchThrowable(
			() -> itemService.modifyItem(saveItem.getId(), request, addImages, principal));
		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("해당 이미지 URL이 존재하지 안습니다.");
	}

	@Test
	@DisplayName("등록된 상품의 상태를 변경하는데 성공한다.")
	void modifyItemStatusTest() {
		// given
		Category category = supportRepository.save(Category.create("식품", "~~~~"));
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", ItemStatus.ON_SALE, category.getId(), null);
		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "pieeeeeee"));
		Item item = supportRepository.save(request1.toEntity(member, "thumbnail"));

		// when
		Item updateItem = supportRepository.findById(item.getId(), Item.class);
		updateItem.changeStatus(ItemStatus.RESERVED);

		// then
		assertThat(updateItem.getStatus()).isEqualTo(ItemStatus.RESERVED);
	}
}
