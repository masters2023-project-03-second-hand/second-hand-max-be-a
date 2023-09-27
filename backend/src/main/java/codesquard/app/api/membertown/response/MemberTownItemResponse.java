package codesquard.app.api.membertown.response;

import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownItemResponse {
	private Long addressId;
	private String fullAddressName;
	private String addressName;
	private Boolean isSelected;

	public static MemberTownItemResponse from(MemberTown memberTown) {
		return new MemberTownItemResponse(
			memberTown.getId(),
			memberTown.getRegion().getName(),
			memberTown.getRegion().getShortAddress(),
			memberTown.isSelected()
		);
	}
}
