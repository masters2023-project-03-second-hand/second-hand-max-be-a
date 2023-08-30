package codesquard.app.api.oauth.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OauthSignUpRequest {

	@NotEmpty(message = "아이디는 띄어쓰기 없이 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.")
	@Pattern(regexp = "^[a-zA-Z0-9]{2,12}$", message = "아이디는 띄어쓰기 없이 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.")
	private String loginId;
	@NotEmpty(message = "동네는 필수 정보입니다.")
	private String addrName;

	private OauthSignUpRequest() {

	}

	private OauthSignUpRequest(String loginId, String addrName) {
		this.loginId = loginId;
		this.addrName = addrName;
	}

	public static OauthSignUpRequest create(String loginId, String addrName) {
		return new OauthSignUpRequest(loginId, addrName);
	}

	public Member toEntity(String avatarUrl, String email) {
		Member member = Member.create(avatarUrl, email, loginId);
		MemberTown town = MemberTown.create(addrName);
		member.addMemberTown(town);
		return member;
	}
}
