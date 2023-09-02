package codesquard.app.api.item.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.image.ImageUploader;
import codesquard.app.api.item.ItemRegisterRequest;
import codesquard.app.api.item.ItemService;
import codesquard.app.domain.item.Item;
import codesquard.app.domain.member.Member;

@SpringBootTest
class ItemServiceTest {

	@Autowired
	private ItemService itemService;
	@Autowired
	private EntityManager em;
	@MockBean
	private ImageUploader imageUploader;

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
}
