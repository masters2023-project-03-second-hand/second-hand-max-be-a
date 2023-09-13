package codesquard.app.api.membertown.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTownRemoveResponse {
	private Long id;
	
	public static MemberTownRemoveResponse create(Long id) {
		return new MemberTownRemoveResponse(id);
	}
}
