package codesquard.app.api.membertown.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownListResponse {
	private List<MemberTownItemResponse> addresses;

	public MemberTownListResponse(List<MemberTownItemResponse> addresses) {
		this.addresses = addresses;
	}
}
