package codesquard.app.domain.membertown;

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
public class MemberTown {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 회원의 동네 등록번호
	private String name; // 동네 이름

	private MemberTown(String name) {
		this.name = name;
	}

	public static MemberTown create(String name) {
		return new MemberTown(name);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, name=%s)", "회원동네", this.getClass().getSimpleName(), id, name);
	}
}
