package codesquard.app.api.member.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.image.ImageUploader;
import codesquard.app.api.member.MemberService;
import codesquard.app.domain.member.Member;

@SpringBootTest
class MemberServiceTest {

	@Autowired
	private MemberService memberService;
	@Autowired
	private EntityManager em;
	@MockBean
	private ImageUploader imageUploader;

	@Test
	@DisplayName("프로필 사진 변경에 성공한다.")
	@Transactional
	void modifyProfileUrl() {

		// given
		given(imageUploader.uploadImageToS3(any(), anyString())).willReturn("test-image.png");
		em.persist(Member.create("123123", "123@123", "pieeeee"));
		MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"test-image",
			"test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			"image-content".getBytes(StandardCharsets.UTF_8));

		// when
		memberService.modifyProfileImage("pieeeee", mockMultipartFile);
		Member member = em.find(Member.class, 1L);

		// then
		assertThat(member.getAvatarUrl()).isEqualTo(mockMultipartFile.getOriginalFilename());
	}
}
