package codesquard.app.api.member;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.*;
import static org.springframework.boot.test.context.SpringBootTest.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import codesquard.app.api.image.ImageUploader;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;
import codesquard.support.SupportRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class MemberServiceTest {

	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private SupportRepository repository;
	@MockBean
	private ImageUploader imageUploader;

	@Test
	@DisplayName("사용자의 프로필 사진 변경에 성공한다.")
	void modifyProfileImage() throws IOException {

		// given
		given(imageUploader.uploadImageToS3(any(), anyString())).willReturn("url");
		willDoNothing().given(imageUploader).deleteImage(anyString());
		repository.save(Member.create("url", "email", "pie"));

		var request = given().log().all()
			.header(HttpHeaders.AUTHORIZATION,
				"Bearer " + jwtProvider.createJwtBasedOnMember(new Member(1L)).getAccessToken())
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
			.multiPart("updateImageFile",
				File.createTempFile("test-image", ".png"),
				MediaType.IMAGE_PNG_VALUE);

		// when
		var response = request
			.when()
			.put("/api/members/pie")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(200);
	}
}
