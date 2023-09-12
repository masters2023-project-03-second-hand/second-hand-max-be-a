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
import codesquard.app.api.membertown.response.MemberTownRemoveResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTown;
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
	private final RegionRepository regionRepository;

	public MemberAddRegionResponse addMemberTown(Principal principal, MemberAddRegionRequest request) {
		log.info("회원 동네 추가 서비스 요청 : 회원아이디={}, 추가할 동네이름={}", principal.getLoginId(), request.getAddressName());

		validateExistFullAddress(request.getFullAddressName());
		validateContainsAddress(request.getFullAddressName(), request.getAddressName());
		validateDuplicateAddress(principal, request);

		Member member = findMemberBy(principal);
		MemberTown town = MemberTown.create(request.getAddressName());

		member.addMemberTown(town);
		return MemberAddRegionResponse.create(town);
	}

	public MemberTownRemoveResponse removeMemberTown(Principal principal, MemberTownRemoveRequest request) {
		log.info("회원 동네 삭제 서비스 요청 : 회원아이디={}, 삭제할 동네이름={}", principal.getLoginId(), request.getAddress());

		String fullAddressName = request.getFullAddress();
		String addressName = request.getAddress();

		validateExistFullAddress(fullAddressName);
		validateContainsAddress(fullAddressName, addressName);

		Member member = findMemberBy(principal);
		Long removeId = member.removeMemberTown(request.getAddress());

		log.info("삭제한 회원동네 등록번호 : {}", removeId);
		return MemberTownRemoveResponse.create(removeId);
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
		Member member = findMemberBy(principal);
		if (member.containsAddressName(request.getAddressName())) {
			throw new RestApiException(MemberTownErrorCode.ALREADY_ADDRESS_NAME);
		}
	}

	private Member findMemberBy(Principal principal) {
		return memberRepository.findById(principal.getMemberId())
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
	}
}
