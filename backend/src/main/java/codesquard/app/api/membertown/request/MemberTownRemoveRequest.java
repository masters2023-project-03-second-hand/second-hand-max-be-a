package codesquard.app.api.membertown.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownRemoveRequest {

	@NotNull(message = "주소 정보는 필수 정보입니다.")
	private Long addressId;

	public static MemberTownRemoveRequest create(Long addressId) {
		return new MemberTownRemoveRequest(addressId);
	}
}
