package codesquard.app.domain.member_town;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberTown {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 회원의 동네 등록번호
	private String name; // 동네 이름

	@Builder
	public MemberTown(String name) {
		this.name = name;
	}
}