package codesquard.app.api.membertown;

import java.util.List;

import org.springframework.stereotype.Component;

import codesquard.app.api.errors.errorcode.MemberTownErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MemberTownValidator {

	private static final int MAXIMUM_MEMBER_TOWN_SIZE = 2;
	private static final int MINIMUM_MEMBER_TOWN_SIZE = 1;

	private final RegionRepository regionRepository;

	public void validateAddMemberTown(List<MemberTown> memberTowns, Region region) {
		validateDuplicateAddress(memberTowns, region);
		validateMaximumMemberTownSize(memberTowns);
	}

	public void validateRemoveMemberTown(List<MemberTown> memberTowns, Region region) {
		validateUnRegisteredAddress(memberTowns, region);
		validateMinimumMemberTownSize(memberTowns);
	}

	private void validateDuplicateAddress(List<MemberTown> memberTowns, Region region) {
		boolean match = memberTowns.stream()
			.map(MemberTown::getRegion)
			.anyMatch(r -> r.equals(region));

		if (match) {
			throw new RestApiException(MemberTownErrorCode.ALREADY_ADDRESS_NAME);
		}
	}

	private void validateUnRegisteredAddress(List<MemberTown> memberTowns, Region region) {
		boolean noneMatch = memberTowns.stream()
			.map(MemberTown::getRegion)
			.noneMatch(r -> r.equals(region));

		if (noneMatch) {
			throw new RestApiException(MemberTownErrorCode.UNREGISTERED_ADDRESS_TO_REMOVE);
		}
	}

	private void validateMaximumMemberTownSize(List<MemberTown> memberTowns) {
		if (memberTowns.size() > MAXIMUM_MEMBER_TOWN_SIZE) {
			throw new RestApiException(MemberTownErrorCode.MAXIMUM_MEMBER_TOWN_SIZE);
		}
	}

	private void validateMinimumMemberTownSize(List<MemberTown> memberTowns) {
		if (memberTowns.size() <= MINIMUM_MEMBER_TOWN_SIZE) {
			throw new RestApiException(MemberTownErrorCode.MINIMUM_MEMBER_TOWN_SIZE);
		}
	}
}
