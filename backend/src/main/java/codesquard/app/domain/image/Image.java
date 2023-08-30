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
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@JoinColumn(name = "item_id")
	@ManyToOne(fetch = FetchType.LAZY) // 이미지를 불러올 때 item 은 불러오지 않는다.
	private Item item;
	private String imageUrl;

	public Image(Item item, String imageUrl) {
		this.item = item;
		this.imageUrl = imageUrl;
	}
}
