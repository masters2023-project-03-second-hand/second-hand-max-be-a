package codesquard.app.api.item;

import static codesquard.app.CategoryTestSupport.*;
import static codesquard.app.ImageTestSupport.*;
import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.RegionTestSupport.*;
import static codesquard.app.domain.item.ItemStatus.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.image.ImageUploader;
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
import codesquard.app.domain.item.ItemRepository;
import codesquard.app.domain.item.ItemStatus;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionRepository;
import codesquard.app.domain.wish.Wish;
import codesquard.app.domain.wish.WishRepository;
import codesquard.support.SupportRepository;

@SpringBootTest
class ItemServiceTest {

	@Autowired
	private ItemService itemService;

	@Autowired
	private SupportRepository supportRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberTownRepository memberTownRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private WishRepository wishRepository;

	@MockBean
	private ImageUploader imageUploader;

	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach
	void tearDown() {
		wishRepository.deleteAllInBatch();
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		memberTownRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

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
		Category category = categoryRepository.save(findByName("스포츠/레저"));
		Member member = memberRepository.save(createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong"));

		Region region = regionRepository.save(createRegion("서울 송파구 가락동"));
		memberTownRepository.save(new MemberTown(region.getShortAddress(), member, region));

		Item item = Item.builder()
			.title("빈티지 롤러 블레이드")
			.content("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")
			.price(200000L)
			.status(ON_SALE)
			.region("가락동")
			.createdAt(now())
			.wishCount(0L)
			.viewCount(0L)
			.chatCount(0L)
			.member(member)
			.category(category)
			.build();
		Item saveItem = itemRepository.save(item);
		List<Image> images = List.of(
			new Image("imageUrlValue1", new Item(saveItem.getId())),
			new Image("imageUrlValue2", new Item(saveItem.getId())));
		List<Image> saveImages = imageRepository.saveAll(images);

		List<MultipartFile> addImages = List.of(createMultipartFile("cat.png"),
			createMultipartFile("roller_blade.jpeg"));
		List<String> deleteImageUrls = saveImages.stream()
			.map(Image::getImageUrl)
			.collect(Collectors.toUnmodifiableList());
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("title", "빈티지 롤러 스케이트");
		requestBody.put("price", 169000);
		requestBody.put("content", "내용");
		requestBody.put("region", "가락동");
		requestBody.put("status", "판매중");
		requestBody.put("categoryId", category.getId());
		requestBody.put("categoryName", category.getName());
		requestBody.put("deleteImageUrls", deleteImageUrls);
		ItemModifyRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			ItemModifyRequest.class);

		given(imageUploader.uploadImageToS3(any(), any()))
			.willReturn("http://s3_image1.com", "http://s3_image2.com");
		// when
		itemService.modifyItem(saveItem.getId(), request, addImages, Principal.from(member));
		// then
		Item modifiedItem = itemRepository.findById(saveItem.getId()).orElseThrow();
		assertAll(() -> {
			assertThat(modifiedItem)
				.extracting("title", "content", "price", "status", "region")
				.contains(request.getTitle(), request.getContent(), request.getPrice(), request.getStatus(),
					request.getRegion());
			assertThat(images).hasSize(2);
		});
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

	@DisplayName("판매자가 한 상품의 상세한 정보를 조회한다")
	@Test
	public void findDetailItemBySeller() {
		// given
		Member member = memberRepository.save(createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong"));
		categoryRepository.saveAll(getCategories());
		Category category = findByName("스포츠/레저");
		Item item = Item.builder()
			.title("빈티지 롤러 블레이드")
			.content("어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.")
			.price(200000L)
			.status(ON_SALE)
			.region("가락동")
			.createdAt(now())
			.wishCount(0L)
			.viewCount(0L)
			.chatCount(0L)
			.member(member)
			.category(category)
			.build();
		Item saveItem = itemRepository.save(item);

		List<Image> images = List.of(
			new Image("imageUrlValue1", new Item(saveItem.getId())),
			new Image("imageUrlValue2", new Item(saveItem.getId())));
		imageRepository.saveAll(images);

		Wish wish = new Wish(member, item, now());
		wishRepository.save(wish);

		// when
		ItemDetailResponse response = itemService.findDetailItemBy(item.getId(), member.getId());
		// then
		SoftAssertions.assertSoftly(softAssertions -> {
			softAssertions.assertThat(response)
				.extracting("isSeller", "seller", "status", "title", "categoryName", "content", "chatCount",
					"wishCount", "viewCount", "price")
				.contains(true, "23Yong", "판매중", "빈티지 롤러 블레이드", "스포츠/레저", "어린시절 추억의향수를 불러 일으키는 롤러 스케이트입니다.", 0L, 0L, 0L,
					200000L);
			softAssertions.assertThat(response.getImageUrls())
				.hasSize(2);
			softAssertions.assertAll();
		});
	}

	@DisplayName("존재하지 않는 상품 등록번호로 상품을 조회할 수 없다")
	@Test
	public void findDetailItemWithNotExistItem() {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		Long itemId = 9999L;
		// when
		Throwable throwable = Assertions.catchThrowable(
			() -> itemService.findDetailItemBy(itemId, member.getId()));
		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("상품을 찾을 수 없습니다.");
	}
}
