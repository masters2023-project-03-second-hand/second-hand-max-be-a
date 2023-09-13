package codesquard.app.api.membertown;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.membertown.request.MemberTownAddRequest;
import codesquard.app.api.membertown.request.MemberTownRemoveRequest;
import codesquard.app.api.membertown.response.MemberAddRegionResponse;
import codesquard.app.api.membertown.response.MemberTownRemoveResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class MemberTownService {

	private final MemberRepository memberRepository;
	private final MemberTownRepository memberTownRepository;
	private final MemberTownValidator validator;

	public MemberAddRegionResponse addMemberTown(Principal principal, MemberTownAddRequest request) {
		log.info("회원 동네 추가 서비스 요청 : 회원아이디={}, 추가할 동네이름={}", principal.getLoginId(), request.getAddress());

		String fullAddress = request.getFullAddress();
		String address = request.getAddress();

		Member member = findMemberBy(principal);
		List<MemberTown> memberTowns = memberTownRepository.findAllByMemberId(principal.getMemberId());
		validator.validateAddMemberTown(memberTowns, fullAddress, address);

		MemberTown town = MemberTown.create(address, member);
		memberTownRepository.save(town);

		return MemberAddRegionResponse.create(town);
	}

	public MemberTownRemoveResponse removeMemberTown(Principal principal, MemberTownRemoveRequest request) {
		log.info("회원 동네 삭제 서비스 요청 : 회원아이디={}, 삭제할 동네이름={}", principal.getLoginId(), request.getAddress());

		String fullAddress = request.getFullAddress();
		String address = request.getAddress();
		List<MemberTown> memberTowns = memberTownRepository.findAllByMemberId(principal.getMemberId());
		validator.validateRemoveMemberTown(memberTowns, fullAddress, address);

		Member member = findMemberBy(principal);
		memberTownRepository.deleteMemberTownByMemberIdAndName(member.getId(), address);

		return MemberTownRemoveResponse.create(address);
	}

	private Member findMemberBy(Principal principal) {
		return memberRepository.findById(principal.getMemberId())
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
	}
}
