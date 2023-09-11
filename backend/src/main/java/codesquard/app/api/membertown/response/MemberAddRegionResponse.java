package codesquard.app.api.membertown.response;

import codesquard.app.domain.membertown.MemberTown;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberAddRegionResponse {
	private Long id;
	private String name;

	private MemberAddRegionResponse(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public static MemberAddRegionResponse create(MemberTown town) {
		return new MemberAddRegionResponse(town.getId(), town.getName());
	}
}
