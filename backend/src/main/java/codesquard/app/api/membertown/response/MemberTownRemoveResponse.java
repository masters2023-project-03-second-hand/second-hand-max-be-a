package codesquard.app.api.membertown.response;

import lombok.Getter;

@Getter
public class MemberTownRemoveResponse {
	private Long id;

	private MemberTownRemoveResponse(Long id) {
		this.id = id;
	}

	public static MemberTownRemoveResponse create(Long id) {
		return new MemberTownRemoveResponse(id);
	}
}
