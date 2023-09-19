package codesquard.app;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import codesquard.app.domain.member.Member;

public class MemberTestSupport {

	public static Member createMember(String avatarUrl, String email, String loginId) {
		return new Member(avatarUrl, email, loginId);
	}

	public static MockMultipartFile createProfile(String name) throws IOException {
		File catFile = new ClassPathResource(name).getFile();
		String filename = catFile.getName().split("\\.")[0];
		String originalFilename = catFile.getName();
		String contentType = "multipart/form-data";
		byte[] content = Files.readAllBytes(catFile.toPath());
		ByteArrayInputStream mockInputStream = new ByteArrayInputStream(content);
		return new MockMultipartFile(filename, originalFilename, contentType, mockInputStream);
	}
}
