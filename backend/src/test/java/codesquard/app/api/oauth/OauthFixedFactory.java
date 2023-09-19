package codesquard.app.api.oauth;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;

public class OauthFixedFactory {
	private static final String LOGIN_ID = "23Yong";
	private static final String EMAIL = "23Yong@naver.com";
	private static final String AVATAR_URL = "avatarUrlValue";

	public static MockMultipartFile createFixedProfile() throws IOException {
		File catFile = new ClassPathResource("cat.png").getFile();
		String filename = catFile.getName().split("\\.")[0];
		String originalFilename = catFile.getName();
		String contentType = "multipart/form-data";
		byte[] content = Files.readAllBytes(catFile.toPath());
		ByteArrayInputStream mockInputStream = new ByteArrayInputStream(content);
		return new MockMultipartFile(filename, originalFilename, contentType, mockInputStream);
	}

	public static LocalDateTime createNow() {
		return LocalDateTime.now();
	}

	public static String createExpectedAccessTokenBy(JwtProvider jwtProvider, Member member,
		LocalDateTime localDateTime) {
		return jwtProvider.createJwtBasedOnMember(member, localDateTime).getAccessToken();
	}

	public static String createExpectedRefreshTokenBy(JwtProvider jwtProvider, Member member,
		LocalDateTime localDateTime) {
		return jwtProvider.createJwtBasedOnMember(member, localDateTime).getRefreshToken();
	}
}
