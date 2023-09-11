package codesquard.app.api.membertown.request;

import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberAddRegionRequest {

	@NotBlank(message = "주소 정보는 필수 정보입니다.")
	private String fullAddressName;
	@NotBlank(message = "주소 정보는 필수 정보입니다.")
	private String addressName;

	private MemberAddRegionRequest(String fullAddressName, String addressName) {
		this.fullAddressName = fullAddressName;
		this.addressName = addressName;
	}

	public static MemberAddRegionRequest create(String fullAddressName, String addressName) {
		return new MemberAddRegionRequest(fullAddressName, addressName);
	}
}
