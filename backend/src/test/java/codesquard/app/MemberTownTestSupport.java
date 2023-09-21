package codesquard.app;

import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.region.Region;

public class MemberTownTestSupport {
	
	public static MemberTown createMemberTown(Member member, Region region, boolean isSelected) {
		return new MemberTown(region.getShortAddress(), member, region, isSelected);
	}
}
