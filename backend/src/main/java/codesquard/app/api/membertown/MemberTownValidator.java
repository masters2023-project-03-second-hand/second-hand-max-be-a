package codesquard.app.api.membertown;

import java.util.List;

import org.springframework.stereotype.Component;

import codesquard.app.api.errors.errorcode.MemberTownErrorCode;
import codesquard.app.api.errors.errorcode.RegionErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.region.RegionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MemberTownValidator {

	private static final int MAXIMUM_MEMBER_TOWN_SIZE = 2;
	private static final int MINIMUM_MEMBER_TOWN_SIZE = 1;

	private final RegionRepository regionRepository;

	public void validateAddMemberTown(List<MemberTown> memberTowns, String fullAddress, String address) {
		validateExistFullAddress(fullAddress);
		validateContainsAddress(fullAddress, address);
		validateDuplicateAddress(memberTowns, address);
		validateMaximumMemberTownSize(memberTowns);
	}

	public void validateRemoveMemberTown(List<MemberTown> memberTowns, String fullAddress, String address) {
		validateExistFullAddress(fullAddress);
		validateContainsAddress(fullAddress, address);
		validateUnRegisteredAddress(memberTowns, address);
		validateMinimumMemberTownSize(memberTowns);
	}

	private void validateExistFullAddress(String fullAddressName) {
		if (!regionRepository.existsRegionByName(fullAddressName)) {
			throw new RestApiException(RegionErrorCode.NOT_FOUND_REGION);
		}
	}

	private void validateContainsAddress(String fullAddressName, String addressName) {
		if (!fullAddressName.contains(addressName)) {
			throw new RestApiException(RegionErrorCode.NOT_MATCH_ADDRESS);
		}
	}

	private void validateDuplicateAddress(List<MemberTown> memberTowns, String address) {
		boolean match = memberTowns.stream()
			.map(MemberTown::getName)
			.anyMatch(name -> name.equals(address));

		if (match) {
			throw new RestApiException(MemberTownErrorCode.ALREADY_ADDRESS_NAME);
		}
	}

	private void validateUnRegisteredAddress(List<MemberTown> memberTowns, String address) {
		boolean noneMatch = memberTowns.stream()
			.map(MemberTown::getName)
			.noneMatch(name -> name.equals(address));

		if (noneMatch) {
			throw new RestApiException(MemberTownErrorCode.UNREGISTERED_ADDRESS_TO_REMOVE);
		}
	}

	private void validateMaximumMemberTownSize(List<MemberTown> memberTowns) {
		if (memberTowns.size() >= MAXIMUM_MEMBER_TOWN_SIZE) {
			throw new RestApiException(MemberTownErrorCode.MAXIMUM_MEMBER_TOWN_SIZE);
		}
	}

	private void validateMinimumMemberTownSize(List<MemberTown> memberTowns) {
		if (memberTowns.size() <= MINIMUM_MEMBER_TOWN_SIZE) {
			throw new RestApiException(MemberTownErrorCode.MINIMUM_MEMBER_TOWN_SIZE);
		}
	}
}
