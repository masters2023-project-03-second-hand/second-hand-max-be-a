package codesquard.app.api.item;

import java.util.ArrayList;
import java.util.List;

import codesquard.app.domain.image.Image;

public class ImageFixedFactory {
	public static List<Image> createFixedImages() {
		List<Image> images = new ArrayList<>();
		images.add(Image.create("http:~~1"));
		images.add(Image.create("http:~~2"));
		return images;
	}
}
