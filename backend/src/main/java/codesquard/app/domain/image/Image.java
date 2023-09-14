package codesquard.app.domain.image;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import codesquard.app.domain.item.Item;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String imageUrl;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	private Image(String imageUrl, Item item) {
		this.imageUrl = imageUrl;
		this.item = item;
	}

	public static Image create(String imageUrl, Item item) {
		return new Image(imageUrl, item);
	}

	public static List<Image> create(List<String> imageUrls, Item item) {
		List<Image> images = new ArrayList<>();
		for (String imageUrl : imageUrls) {
			images.add(create(imageUrl, item));
		}
		return images;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, imageUrl=%s, item_id=%d)",
			"상품의 이미지", this.getClass().getSimpleName(), id, imageUrl, item.getId());
	}
}
