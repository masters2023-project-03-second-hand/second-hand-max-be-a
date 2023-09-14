package codesquard.app.api.item;

import static java.nio.file.Files.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.domain.image.Image;
import codesquard.app.domain.item.Item;

public class ImageFixedFactory {
	public static List<Image> createFixedImages(Item item) {
		List<Image> images = new ArrayList<>();
		images.add(Image.create(
			"https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/%E1%84%85%E1%85%A9%E1%86%AF%E1%84%85%E1%85%A5%E1%84%87%E1%85%B3%E1%86%AF%E1%84%85%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3.webp",
			item));
		images.add(Image.create(
			"https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/%E1%84%85%E1%85%A9%E1%86%AF%E1%84%85%E1%85%A5%E1%84%87%E1%85%B3%E1%86%AF%E1%84%85%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B4.webp",
			item));
		return images;
	}

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
