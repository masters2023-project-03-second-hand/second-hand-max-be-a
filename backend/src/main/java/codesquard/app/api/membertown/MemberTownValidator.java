package codesquard.app.api.membertown;

import org.springframework.stereotype.Component;

import codesquard.app.api.errors.errorcode.MemberTownErrorCode;
import codesquard.app.api.errors.errorcode.RegionErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.region.RegionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MemberTownValidator {

	private final RegionRepository regionRepository;

	public void validateAddMemberTown(Member member, String fullAddress, String address) {
		validateExistFullAddress(fullAddress);
		validateContainsAddress(fullAddress, address);
		validateDuplicateAddress(member, address);
	}

	public void validateRemoveMemberTown(String fullAddress, String address) {
		validateExistFullAddress(fullAddress);
		validateContainsAddress(fullAddress, address);
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

	private void validateDuplicateAddress(Member member, String address) {
		if (member.containsAddressName(address)) {
			throw new RestApiException(MemberTownErrorCode.ALREADY_ADDRESS_NAME);
		}
	}

}
