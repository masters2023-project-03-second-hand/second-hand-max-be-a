package codesquard.app.api.image;

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

		String fileName = UPLOADED_IMAGES_DIR + imageFile.getFileName();
		return imageUploader.uploadImageToS3(imageFile, fileName);
	}
}
