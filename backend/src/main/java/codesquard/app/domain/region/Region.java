package codesquard.app.domain.region;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Region {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;

	private Region(String name) {
		this.name = name;
	}

	public static Region create(String name) {
		return new Region(name);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, name=%s)", "지역", this.getClass().getSimpleName(), id, name);
	}
}
