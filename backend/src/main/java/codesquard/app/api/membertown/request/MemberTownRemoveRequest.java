package codesquard.app.api.membertown.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownRemoveRequest {
	private String fullAddress;
	private String address;
}
