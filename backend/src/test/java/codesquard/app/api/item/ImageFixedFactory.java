package codesquard.app.api.item;

import java.util.ArrayList;
import java.util.List;

import codesquard.app.domain.image.Image;

public class ImageFixedFactory {
	public static List<Image> createFixedImages() {
		List<Image> images = new ArrayList<>();
		images.add(Image.create(
			"https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/%E1%84%85%E1%85%A9%E1%86%AF%E1%84%85%E1%85%A5%E1%84%87%E1%85%B3%E1%86%AF%E1%84%85%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3.webp"));
		images.add(Image.create(
			"https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/%E1%84%85%E1%85%A9%E1%86%AF%E1%84%85%E1%85%A5%E1%84%87%E1%85%B3%E1%86%AF%E1%84%85%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3.webp"));
		return images;
	}
}
