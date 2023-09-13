package codesquard.app.api.membertown.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownRemoveResponse {
	private String address;

	public static MemberTownRemoveResponse create(String address) {
		return new MemberTownRemoveResponse(address);
	}
}
