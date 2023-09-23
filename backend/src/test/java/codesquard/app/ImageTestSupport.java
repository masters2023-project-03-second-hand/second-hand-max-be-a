package codesquard.app;

import static java.nio.file.Files.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

public class ImageTestSupport {

	public static MockMultipartFile createMultipartFile(String path) throws IOException {
		File file = new ClassPathResource(path).getFile();
		String filename = file.getName().split("\\.")[0];
		String originalFilename = file.getName();
		String contentType = "multipart/form-data";
		byte[] content = readAllBytes(file.toPath());
		ByteArrayInputStream mockInputStream = new ByteArrayInputStream(content);
		return new MockMultipartFile(filename, originalFilename, contentType, mockInputStream);
	}
}
