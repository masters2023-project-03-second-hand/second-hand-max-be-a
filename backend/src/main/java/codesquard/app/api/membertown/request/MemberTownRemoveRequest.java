package codesquard.app.api.membertown.request;

import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownRemoveRequest {
	@NotBlank(message = "주소 정보는 필수 정보입니다.")
	private String fullAddress;
	@NotBlank(message = "주소 정보는 필수 정보입니다.")
	private String address;

	public static MemberTownRemoveRequest create(String fullAddress, String address) {
		return new MemberTownRemoveRequest(fullAddress, address);
	}
}
