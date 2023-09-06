package codesquard.app.domain.category;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@Column(name = "image_url")
	private String imageUrl;

	public Category(String name, String imageUrl) {
		this.name = name;
		this.imageUrl = imageUrl;
	}

	public static Category create(String name, String imageUrl) {
		return new Category(name, imageUrl);
	}
}
