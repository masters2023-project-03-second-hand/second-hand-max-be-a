package codesquard.app.api.membertown.response;

import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberAddRegionResponse {
	private Long id;
	private String name;

	public static MemberAddRegionResponse from(MemberTown town) {
		return new MemberAddRegionResponse(town.getId(), town.getName());
	}
}
