package codesquard.app.api.oauth.request;

import java.util.List;

import codesquard.app.annotation.Addresses;
import codesquard.app.annotation.LoginId;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthSignUpRequest {

	@LoginId
	private String loginId;

	@Addresses
	private List<String> addressNames;

	private OauthSignUpRequest(String loginId, List<String> addressNames) {
		this.loginId = loginId;
		this.addressNames = addressNames;
	}

	public static OauthSignUpRequest create(String loginId, List<String> addressNames) {
		return new OauthSignUpRequest(loginId, addressNames);
	}

	public Member toEntity(String avatarUrl, String email) {
		Member member = Member.create(avatarUrl, email, loginId);
		addressNames.stream()
			.map(MemberTown::create)
			.forEach(member::addMemberTown);
		return member;
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s, addressName=%s)", "회원가입 요청", this.getClass().getSimpleName(), loginId,
			addressNames);
	}
}
