package codesquard.app.api.oauth.response;

import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownLoginResponse {
	private Long addressId;
	private String fullAddressName;
	private String addressName;
	private Boolean isSelected;

	public static MemberTownLoginResponse from(MemberTown memberTown) {
		return new MemberTownLoginResponse(
			memberTown.getRegion().getId(),
			memberTown.getRegion().getName(),
			memberTown.getName(),
			memberTown.isSelected());
	}
}
