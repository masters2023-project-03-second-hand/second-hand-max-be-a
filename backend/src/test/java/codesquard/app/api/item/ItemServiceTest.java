package codesquard.app.api.item;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.image.ImageUploader;
import codesquard.app.api.response.ItemResponse;
import codesquard.app.api.response.ItemResponses;
import codesquard.app.domain.category.Category;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.support.SupportRepository;

@SpringBootTest
class ItemServiceTest extends IntegrationTestSupport {

	@Autowired
	private ItemService itemService;
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
			"선풍기", 12000L, null, "가양 1동", "판매중", category.getId(), null);

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
			"선풍기", 12000L, null, "가양 1동", "판매중", category.getId(), null);
		ItemRegisterRequest request2 = new ItemRegisterRequest(
			"전기밥솥", null, null, "가양 1동", "판매중", category.getId(), null);
		ItemRegisterRequest request3 = new ItemRegisterRequest(
			"노트북", null, null, "가양 1동", "판매중", category.getId(), null);

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
}
