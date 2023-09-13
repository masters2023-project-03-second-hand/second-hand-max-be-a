package codesquard.app.api.oauth.request;

import java.util.List;

import codesquard.app.annotation.Addresses;
import codesquard.app.annotation.LoginId;
import codesquard.app.domain.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthSignUpRequest {

	@LoginId
	private String loginId;

	@Addresses
	private List<Long> addressIds;

	public static OauthSignUpRequest create(String loginId, List<Long> addressIds) {
		return new OauthSignUpRequest(loginId, addressIds);
	}

	public Member toEntity(String avatarUrl, String email) {
		return Member.create(avatarUrl, email, loginId);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s, addressName=%s)", "회원가입 요청", this.getClass().getSimpleName(), loginId,
			addressIds);
	}
}
