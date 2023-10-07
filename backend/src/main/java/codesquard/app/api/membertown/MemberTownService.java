package codesquard.app.api.membertown;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.MemberTownErrorCode;
import codesquard.app.api.errors.errorcode.RegionErrorCode;
import codesquard.app.api.errors.exception.BadRequestException;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.api.membertown.request.MemberTownAddRequest;
import codesquard.app.api.membertown.request.MemberTownRemoveRequest;
import codesquard.app.api.membertown.response.MemberAddRegionResponse;
import codesquard.app.api.membertown.response.MemberTownItemResponse;
import codesquard.app.api.membertown.response.MemberTownListResponse;
import codesquard.app.api.membertown.response.MemberTownRemoveResponse;
import codesquard.app.api.region.request.RegionSelectionRequest;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberTownService {

	private final MemberRepository memberRepository;
	private final MemberTownRepository memberTownRepository;
	private final RegionRepository regionRepository;
	private final MemberTownValidator memberTownValidator;

	@Transactional
	public MemberAddRegionResponse addMemberTown(Principal principal, MemberTownAddRequest request) {
		log.info("회원 동네 추가 서비스 요청 : 회원아이디={}, 추가할 동네 등록번호={}", principal.getLoginId(), request.getAddressId());

		Member member = findMemberBy(principal);
		List<MemberTown> memberTowns = memberTownRepository.findAllByMemberId(principal.getMemberId());
		Region region = getAddressIdBy(request.getAddressId());
		memberTownValidator.validateAddMemberTown(memberTowns, region);

		MemberTown town = MemberTown.notSelectedMemberTown(region, member);
		memberTownRepository.save(town);

		return MemberAddRegionResponse.from(town);
	}

	@Transactional
	public MemberTownRemoveResponse removeMemberTown(Principal principal, MemberTownRemoveRequest request) {
		log.info("회원 동네 삭제 서비스 요청 : 회원아이디={}, 삭제할 동네 등록번호={}", principal.getLoginId(), request.getAddressId());

		List<MemberTown> memberTowns = memberTownRepository.findAllByMemberId(principal.getMemberId());
		Region region = getAddressIdBy(request.getAddressId());
		memberTownValidator.validateRemoveMemberTown(memberTowns, region);

		Member member = findMemberBy(principal);
		memberTownRepository.deleteMemberTownByMemberIdAndRegionId(member.getId(), region.getId());

		changeIsSelectedWithRemainMemberTown(principal);

		return MemberTownRemoveResponse.create(region.getName());
	}

	private void changeIsSelectedWithRemainMemberTown(Principal principal) {
		MemberTown remainMemberTown = memberTownRepository.findAllByMemberId(principal.getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new NotFoundResourceException(MemberTownErrorCode.NOT_FOUND_MEMBER_TOWN));
		remainMemberTown.changeIsSelected(true);
	}

	private Region getAddressIdBy(Long addressId) {
		return regionRepository.findById(addressId)
			.orElseThrow(() -> new NotFoundResourceException(RegionErrorCode.NOT_FOUND_REGION));
	}

	private Member findMemberBy(Principal principal) {
		return memberRepository.findById(principal.getMemberId())
			.orElseThrow(() -> new NotFoundResourceException(MemberErrorCode.NOT_FOUND_MEMBER));
	}

	@Transactional
	public void selectRegion(RegionSelectionRequest request, Principal principal) {
		validateExistRegion(request.getSelectedAddressId());
		validateRegisteredMemberTown(request.getSelectedAddressId(), principal.getMemberId());

		Long regionId = memberTownRepository.findRegionIdByMemberIdAndIsSelected(
			principal.getMemberId(), true);
		int result = memberTownRepository.changeIsSelect(false, regionId, principal.getMemberId());
		log.debug("지역 선택 해제 결과 : result={}", result);

		result = memberTownRepository.changeIsSelect(true, request.getSelectedAddressId(), principal.getMemberId());
		log.debug("지역 선택 활성화 결과 : result={}", result);
	}

	private void validateRegisteredMemberTown(Long regionId, Long memberId) {
		if (memberTownRepository.findMemberTownByMemberIdAndRegionId(memberId, regionId).isEmpty()) {
			throw new BadRequestException(MemberTownErrorCode.NOT_SELECT_UNREGISTERED_MEMBER_TOWN);
		}
	}

	private void validateExistRegion(Long regionId) {
		if (regionRepository.findById(regionId).isEmpty()) {
			throw new NotFoundResourceException(RegionErrorCode.NOT_FOUND_REGION);
		}
	}

	public MemberTownListResponse readAll(Principal principal) {
		List<MemberTownItemResponse> itemResponses = memberTownRepository.findAllByMemberId(principal.getMemberId())
			.stream()
			.map(MemberTownItemResponse::from)
			.collect(Collectors.toUnmodifiableList());
		return new MemberTownListResponse(itemResponses);
	}
}
