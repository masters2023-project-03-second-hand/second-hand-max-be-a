package codesquard.app.domain.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.errors.errorcode.ImageErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageFile {

	private final String fileName;
	private final String contentType;
	private final Long fileSize;
	private final InputStream imageInputStream;

	public ImageFile(MultipartFile multipartFile) {
		this.fileName = getFileName(multipartFile);
		this.contentType = getImageContentType(multipartFile);
		this.imageInputStream = getImageInputStream(multipartFile);
		this.fileSize = multipartFile.getSize();
	}

	private String getFileName(MultipartFile multipartFile) {
		String ext = extractExt(multipartFile.getOriginalFilename());
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + ext;
	}

	private String extractExt(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}

	private String getImageContentType(MultipartFile multipartFile) {
		return ImageContentType.findEnum(multipartFile.getContentType()).getContentType();
	}

	public InputStream getImageInputStream(MultipartFile multipartFile) {
		try {
			return multipartFile.getInputStream();
		} catch (IOException e) {
			throw new RestApiException(ImageErrorCode.FILE_IO_EXCEPTION);
		}
	}

	public static ImageFile from(MultipartFile multipartFile) {
		return new ImageFile(multipartFile);
	}

	public static List<ImageFile> from(List<MultipartFile> files) {
		List<ImageFile> imageFiles = new ArrayList<>();
		for (MultipartFile multipartFile : files) {
			if (!multipartFile.isEmpty()) {
				imageFiles.add(new ImageFile(multipartFile));
			}
		}
		return imageFiles;
	}

	@Getter
	@RequiredArgsConstructor
	enum ImageContentType {

		JPEG("image/jpeg"),
		JPG("image/jpg"),
		PNG("image/png"),
		SVG("image/svg");

		private final String contentType;

		public static ImageContentType findEnum(String contentType) {
			for (ImageContentType imageContentType : ImageContentType.values()) {
				if (imageContentType.getContentType().equals(contentType)) {
					return imageContentType;
				}
			}
			throw new RestApiException(ImageErrorCode.INVALID_FILE_EXTENSION);
		}
	}
}
