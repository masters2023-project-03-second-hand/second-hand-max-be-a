package codesquard.app.api.oauth.request;

import javax.validation.constraints.Pattern;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.member_town.MemberTown;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class OauthSignUpRequest {
	@Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$", message = "아이디는 띄어쓰기 없이 한글, 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.")
	private String loginId;
	private String addrName;

	public OauthSignUpRequest(String loginId, String addrName) {
		this.loginId = loginId;
		this.addrName = addrName;
	}

	public Member toEntity(String socialLoginId) {
		Member member = Member.builder()
			.socialLoginId(socialLoginId)
			.nickname(loginId)
			.build();
		MemberTown town = MemberTown.builder()
			.name(addrName)
			.build();
		member.addMemberTown(town);
		return member;
	}
}
