package codesquard.app.api.member.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.image.ImageUploader;
import codesquard.app.api.member.MemberService;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.member.Member;

class MemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private MemberService memberService;
	@MockBean
	private ImageUploader imageUploader;

	@BeforeEach
	void cleanup() {
		chatLogRepository.deleteAllInBatch();
		chatRoomRepository.deleteAllInBatch();
		interestRepository.deleteAllInBatch();
		imageRepository.deleteAllInBatch();
		itemRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		memberTownRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("프로필 사진 변경에 성공한다.")
	@Transactional
	void modifyProfileUrl() {

		// given
		given(imageUploader.uploadImageToS3(any(), anyString())).willReturn("test-image.png");
		Member saveMember = memberRepository.save(OauthFixedFactory.createFixedMemberWithMemberTown());
		MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"test-image",
			"test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			"image-content".getBytes(StandardCharsets.UTF_8));

		// when
		memberService.modifyProfileImage("23Yong", mockMultipartFile);
		Member member = memberRepository.findById(saveMember.getId()).orElseThrow();

		// then
		assertThat(member.getAvatarUrl()).isEqualTo(mockMultipartFile.getOriginalFilename());
	}
}
