package codesquard.app.domain.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.errors.errorcode.ImageErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageFile {

	private static final Logger log = LoggerFactory.getLogger(ImageFile.class);

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

	private String getFileName(MultipartFile multipartFile) {
		String ext = extractExt(multipartFile.getOriginalFilename());
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + ext;
	}

	private String extractExt(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}

	/**
	 * 파일 이름에서 확장자를 가져옵니다.
	 * ex) cat.png -> png
	 */
	private String getImageContentType(MultipartFile multipartFile) {
		return ImageContentType.findEnum(StringUtils.getFilenameExtension(multipartFile.getOriginalFilename()));
	}

	public InputStream getImageInputStream(MultipartFile multipartFile) {
		try {
			return multipartFile.getInputStream();
		} catch (IOException e) {
			throw new RestApiException(ImageErrorCode.FILE_IO_EXCEPTION);
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum ImageContentType {

		JPEG("jpeg"),
		JPG("jpg"),
		PNG("png"),
		SVG("svg");

		private final String contentType;

		public static String findEnum(String contentType) {
			log.info("ContentType : {}", contentType);

			for (ImageContentType imageContentType : ImageContentType.values()) {
				if (imageContentType.getContentType().equals(contentType.toLowerCase())) {
					return imageContentType.getContentType();
				}
			}
			throw new RestApiException(ImageErrorCode.INVALID_FILE_EXTENSION);
		}
	}
}
