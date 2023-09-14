package codesquard.app.domain.region;

import java.util.Arrays;
import java.util.stream.Collectors;

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

	public String getShortAddress() {
		final String space = " ";
		return Arrays.stream(name.split(space))
			.skip(2)
			.collect(Collectors.joining(space));
	}
}
