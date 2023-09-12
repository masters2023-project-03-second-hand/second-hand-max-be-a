package codesquard.app.api.membertown.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownRemoveRequest {
	private String fullAddress;
	private String address;

	private MemberTownRemoveRequest(String fullAddress, String address) {
		this.fullAddress = fullAddress;
		this.address = address;
	}

	public static MemberTownRemoveRequest create(String fullAddress, String address) {
		return new MemberTownRemoveRequest(fullAddress, address);
	}
}
