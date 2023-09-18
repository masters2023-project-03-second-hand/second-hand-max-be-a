package codesquard.app.api.oauth.response;

import java.util.List;

import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthLoginResponse {

	private Jwt jwt;
	private OauthLoginMemberResponse user;

	public static OauthLoginResponse of(Jwt jwt, Member member, List<MemberTown> memberTowns) {
		OauthLoginMemberResponse user = OauthLoginMemberResponse.of(member, memberTowns);
		return new OauthLoginResponse(jwt, user);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s)", "소셜 로그인 응답", this.getClass().getSimpleName(), user.getLoginId());
	}
}
