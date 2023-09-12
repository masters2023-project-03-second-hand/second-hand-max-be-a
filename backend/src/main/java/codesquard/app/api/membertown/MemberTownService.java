package codesquard.app.api.membertown;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.MemberTownErrorCode;
import codesquard.app.api.errors.errorcode.RegionErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.membertown.request.MemberAddRegionRequest;
import codesquard.app.api.membertown.request.MemberTownRemoveRequest;
import codesquard.app.api.membertown.response.MemberAddRegionResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.region.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class MemberTownService {

	private final MemberRepository memberRepository;
	private final MemberTownRepository memberTownRepository;
	private final RegionRepository regionRepository;

	public MemberAddRegionResponse addMemberTown(Principal principal, MemberAddRegionRequest request) {
		validateExistFullAddress(request.getFullAddressName());
		validateContainsAddress(request.getFullAddressName(), request.getAddressName());
		validateDuplicateAddress(principal, request);

		Member member = memberRepository.findById(principal.getMemberId())
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		MemberTown town = MemberTown.create(request.getAddressName());

		member.addMemberTown(town);
		return MemberAddRegionResponse.create(town);
	}

	public boolean removeMemberTown(Principal principal, MemberTownRemoveRequest request) {
		String fullAddressName = request.getFullAddress();
		String addressName = request.getAddress();

		validateExistFullAddress(fullAddressName);
		validateContainsAddress(fullAddressName, addressName);

		Member member = memberRepository.findById(principal.getMemberId())
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		boolean result = member.removeMemberTown(request.getAddress());

		if (!result) {
			throw new RestApiException(MemberTownErrorCode.FAIL_REMOVE_ADDRESS);
		}
		return result;
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

	private void validateDuplicateAddress(Principal principal, MemberAddRegionRequest request) {
		Member member = memberRepository.findById(principal.getMemberId())
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		if (member.containsAddressName(request.getAddressName())) {
			throw new RestApiException(MemberTownErrorCode.ALREADY_ADDRESS_NAME);
		}
	}
}
