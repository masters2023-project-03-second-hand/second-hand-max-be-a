package codesquard.app.api.image;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.domain.image.ImageFile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

	private static final String UPLOADED_IMAGES_DIR = "public/";

	private final ImageUploader imageUploader;

	@Transactional
	public String uploadImage(MultipartFile file) {
		ImageFile imageFile = ImageFile.from(file);

		String filePath = UPLOADED_IMAGES_DIR + imageFile.getFileName();
		return imageUploader.uploadImageToS3(imageFile, filePath);
	}

	@Transactional
	public List<String> uploadImages(List<MultipartFile> files) {
		List<ImageFile> imageFiles = ImageFile.from(files);
		List<String> urls = new ArrayList<>();
		for (ImageFile imageFile : imageFiles) {
			String fileName = UPLOADED_IMAGES_DIR + imageFile.getFileName();
			urls.add(imageUploader.uploadImageToS3(imageFile, fileName));
		}
		return urls;
	}

	@Transactional
	public void deleteImage(String fileName) {
		imageUploader.deleteImage(fileName);
	}
}
