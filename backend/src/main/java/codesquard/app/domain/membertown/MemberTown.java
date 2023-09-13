package codesquard.app.domain.membertown;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import codesquard.app.domain.member.Member;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public MemberTown(String name, Member member) {
		this.name = name;
		this.member = member;
	}

	public static MemberTown create(String name, Member member) {
		return new MemberTown(name, member);
	}

	public static List<MemberTown> create(List<String> names, Member member) {
		List<MemberTown> memberTowns = new ArrayList<>();
		for (String name : names) {
			memberTowns.add(create(name, member));
		}
		return memberTowns;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(id=%d, name=%s)", "회원동네", this.getClass().getSimpleName(), id, name);
	}
}
