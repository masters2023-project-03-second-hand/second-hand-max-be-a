package codesquard.app.domain.member;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(exclude = "towns")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 회원 등록번호
	private String avatarUrl; // 프로필 사진
	private String socialLoginId; // 소셜 로그인 아이디
	private String nickname; // 닉네임

	@OneToMany(cascade = CascadeType.ALL)
	private List<MemberTown> towns = new ArrayList<>(); // 동네

	@Builder
	public Member(String avatarUrl, String socialLoginId, String nickname) {
		this.avatarUrl = avatarUrl;
		this.socialLoginId = socialLoginId;
		this.nickname = nickname;
		this.towns = new ArrayList<>();
	}

	public void addMemberTown(MemberTown town) {
		towns.add(town);
	}
}
