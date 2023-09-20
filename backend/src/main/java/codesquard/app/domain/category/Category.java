package codesquard.app.domain.category;

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
	private String imageUrl;

	public Category(Long id) {
		this.id = id;
	}

	public Category(String name, String imageUrl) {
		this.name = name;
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, name=%s)", "카테고리", this.getClass().getSimpleName(), id, name);
	}

}
