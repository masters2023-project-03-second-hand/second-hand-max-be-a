package codesquard.app.api.oauth.request;

import static codesquard.app.config.ValidationGroups.*;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthSignUpRequest {

	@NotBlank(message = "로그인 아이디는 필수 정보입니다.", groups = NotBlankGroup.class)
	@Pattern(regexp = "^[a-zA-Z0-9]{2,12}$", message = "아이디는 띄어쓰기 없이 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.", groups = PatternGroup.class)
	private String loginId;
	@NotNull(message = "주소 이름은 공백이면 안됩니다.", groups = NotNullGroup.class)
	@Size(min = 1, max = 2, message = "동네 주소는 최소 1개에서 최대 2개까지 추가할 수 있습니다.", groups = SizeGroup.class)
	private List<@NotBlank(message = "주소 이름은 공백이면 안됩니다.", groups = NotBlankGroup.class) String> addressNames;

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
