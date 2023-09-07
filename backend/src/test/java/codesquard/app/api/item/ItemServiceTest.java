package codesquard.app.api.item;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.image.ImageUploader;
import codesquard.app.api.response.ItemListResponse;
import codesquard.app.api.response.ItemResponse;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;
import codesquard.support.DatabaseInitializer;
import codesquard.support.SupportRepository;

@SpringBootTest
class ItemServiceTest {

	@Autowired
	private ItemService itemService;
	@Autowired
	private EntityManager em;
	@Autowired
	private SupportRepository supportRepository;
	@Autowired
	private DatabaseInitializer databaseInitializer;
	@MockBean
	private ImageUploader imageUploader;

	@AfterEach
	public void tearDown() {
		databaseInitializer.truncateTables();
	}

	@Test
	@DisplayName("새로운 상품 등록에 성공한다.")
	@Transactional
	void registerTest() {
		// given
		given(imageUploader.uploadImageToS3(any(), anyString())).willReturn("url");
		em.persist(Member.create("avatar", "pie@pie", "pieeeeeee"));
		List<MultipartFile> multipartFiles = getMultipartFiles();
		ItemRegisterRequest request = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", "판매중", 1L, null);

		// when
		itemService.register(request, multipartFiles, 1L);
		Item item = em.find(Item.class, 1L);

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
		ItemRegisterRequest request1 = new ItemRegisterRequest(
			"선풍기", 12000L, null, "가양 1동", "판매중", 1L, null);
		ItemRegisterRequest request2 = new ItemRegisterRequest(
			"전기밥솥", null, null, "가양 1동", "판매중", 1L, null);
		ItemRegisterRequest request3 = new ItemRegisterRequest(
			"노트북", null, null, "가양 1동", "판매중", 1L, null);

		Member member = supportRepository.save(Member.create("avatar", "pie@pie", "pieeeeeee"));
		supportRepository.save(Item.toEntity(request1, member, "thumbnail"));
		supportRepository.save(Item.toEntity(request2, member, "thumbnail"));
		supportRepository.save(Item.toEntity(request3, member, "thumbnail"));

		// when
		ItemListResponse all = itemService.findAll("가양 1동", 2, null, null);

		// then
		List<ItemResponse> contents = all.getContents();
		assertAll(
			() -> assertThat(contents.size()).isEqualTo(2),
			() -> assertThat(contents.get(0).getTitle()).isEqualTo("노트북"),
			() -> assertThat(all.getPaging().isHasNext()).isTrue(),
			() -> assertThat(all.getPaging().getNextCursor()).isEqualTo(2L));
	}
}
