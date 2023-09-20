package codesquard.app.domain.image;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
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
	@Column(name = "thumbnail", nullable = false)
	private boolean thumbnail;

	public Image(String imageUrl, Item item, boolean thumbnail) {
		this.imageUrl = imageUrl;
		this.item = item;
		this.thumbnail = thumbnail;
	}

	public static List<Image> createImages(List<String> imageUrls, Item item) {
		List<Image> images = new ArrayList<>();
		for (String imageUrl : imageUrls) {
			images.add(new Image(imageUrl, item, false));
		}
		return images;
	}

	public static Image thumbnail(String imageUrl, Long itemId) {
		return new Image(imageUrl, new Item(itemId), true);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, imageUrl=%s, item_id=%d)",
			"상품의 이미지", this.getClass().getSimpleName(), id, imageUrl, item.getId());
	}
}
