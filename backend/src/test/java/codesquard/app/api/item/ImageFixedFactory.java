package codesquard.app.api.item;

import static java.nio.file.Files.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ImageFixedFactory {

	public static List<MultipartFile> createFixedMultipartFile() throws IOException {
		return List.of(createMultipartFile("cat.png"), createMultipartFile("roller_blade.jpeg"));
	}

	private static MultipartFile createMultipartFile(String path) throws IOException {
		File catFile = new ClassPathResource(path).getFile();
		String filename = catFile.getName().split("\\.")[0];
		String originalFilename = catFile.getName();
		String contentType = "multipart/form-data";
		byte[] content = readAllBytes(catFile.toPath());
		ByteArrayInputStream mockInputStream = new ByteArrayInputStream(content);
		return new MockMultipartFile(filename, originalFilename, contentType, mockInputStream);
	}
}
