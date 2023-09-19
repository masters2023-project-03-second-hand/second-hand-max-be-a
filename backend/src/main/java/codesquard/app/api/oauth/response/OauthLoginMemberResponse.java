package codesquard.app.api.oauth.response;

import java.util.List;
import java.util.stream.Collectors;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthLoginMemberResponse {
	private String loginId;
	private String profileUrl;
	private List<MemberTownLoginResponse> addresses;

	private OauthLoginMemberResponse(Member member, List<MemberTown> memberTowns) {
		this.loginId = member.getLoginId();
		this.profileUrl = member.getAvatarUrl();
		this.addresses = memberTowns.stream()
			.map(MemberTownLoginResponse::from)
			.collect(Collectors.toUnmodifiableList());
	}

	public static OauthLoginMemberResponse of(Member member, List<MemberTown> memberTowns) {
		return new OauthLoginMemberResponse(member, memberTowns);
	}

	@Override
	public String toString() {
		return String.format("%s, %s(loginId=%s)", "로그인 회원정보 응답", this.getClass().getSimpleName(), loginId);
	}
}
