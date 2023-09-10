package codesquard.app.domain.image;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import codesquard.app.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;
	private String imageUrl;

	public Image(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Image(Item item, String imageUrl) {
		this.item = item;
		this.imageUrl = imageUrl;
	}

	public static Image create(String imageUrl) {
		return new Image(imageUrl);
	}

	public void changeItem(Item item) {
		this.item = item;
		addImageBy(item);
	}

	private void addImageBy(Item item) {
		if (item == null) {
			return;
		}
		if (!item.containsImage(this)) {
			item.addImage(this);
		}
	}
}
