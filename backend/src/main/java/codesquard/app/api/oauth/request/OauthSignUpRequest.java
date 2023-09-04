package codesquard.app.api.oauth.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import lombok.Getter;

@Getter
public class OauthSignUpRequest {

	@NotBlank(message = "로그인 아이디는 필수 정보입니다.")
	@Pattern(regexp = "^[a-zA-Z0-9]{2,12}$", message = "아이디는 띄어쓰기 없이 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.")
	private String loginId;
	@NotBlank(message = "동네 이름은 필수 정보입니다.")
	private String addressName;

	private OauthSignUpRequest() {

	}

	private OauthSignUpRequest(String loginId, String addressName) {
		this.loginId = loginId;
		this.addressName = addressName;
	}

	public static OauthSignUpRequest create(String loginId, String addrName) {
		return new OauthSignUpRequest(loginId, addrName);
	}

	public Member toEntity(String avatarUrl, String email) {
		Member member = Member.create(avatarUrl, email, loginId);
		MemberTown town = MemberTown.create(addressName);
		member.addMemberTown(town);
		return member;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s, addressName=%s)", "회원가입 요청", this.getClass().getSimpleName(), loginId,
			addressName);
	}
}
