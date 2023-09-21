package codesquard.app.api.oauth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	private boolean isSelected;

	public static MemberTownLoginResponse from(MemberTown memberTown) {
		return new MemberTownLoginResponse(
			memberTown.getRegion().getId(),
			memberTown.getRegion().getName(),
			memberTown.getName(),
			memberTown.isSelected());
	}

	@JsonProperty("isSelected")
	public boolean isSelected() {
		return isSelected;
	}
}
