package codesquard.app.api.membertown.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownAddRequest {

	@NotNull(message = "주소 정보는 필수 정보입니다.")
	private Long addressId;
}
