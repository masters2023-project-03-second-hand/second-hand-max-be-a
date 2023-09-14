package codesquard.app.api.membertown;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import codesquard.app.IntegrationTestSupport;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.membertown.request.MemberTownAddRequest;
import codesquard.app.api.membertown.request.MemberTownRemoveRequest;
import codesquard.app.api.membertown.response.MemberAddRegionResponse;
import codesquard.app.api.membertown.response.MemberTownRemoveResponse;
import codesquard.app.api.oauth.OauthFixedFactory;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.oauth.support.Principal;
import codesquard.app.domain.region.Region;

class MemberTownServiceTest extends IntegrationTestSupport {

	@DisplayName("선택한 동네를 회원 동네에 추가한다")
	@Test
	public void addMemberTown() {
		// given
		Member saveMember = memberRepository.save(OauthFixedFactory.createFixedMember());
		Principal principal = Principal.from(saveMember);
		Long addressId = getRegion("서울 송파구 가락동").getId();
		MemberTownAddRequest request = MemberTownAddRequest.create(addressId);
		// when
		MemberAddRegionResponse response = memberTownService.addMemberTown(principal, request);
		// then
		List<MemberTown> memberTowns = memberTownRepository.findAll();
		assertAll(() -> {
			assertThat(response.getName()).isEqualTo("가락동");
			assertThat(memberTowns).hasSize(1);
		});
	}

	@DisplayName("주소에 없는 동네를 회원 동네에 추가할 수 없다")
	@Test
	public void addMemberTownWithNotExistFullAddressName() {
		// given
		Member saveMember = memberRepository.save(OauthFixedFactory.createFixedMember());
		Principal principal = Principal.from(saveMember);
		MemberTownAddRequest request = MemberTownAddRequest.create(9999L);
		// when & then
		assertThatThrownBy(() -> memberTownService.addMemberTown(principal, request))
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("주소를 찾지 못하였습니다.");
	}

	@DisplayName("회원의 동네 추가시 동네 등록 최대개수를 초과하여서 회원의 동네를 추가할 수 없다")
	@Test
	public void addMemberTownWithOverTheMaximumMemberTownSize() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		List<Region> regions = getRegions(List.of("서울 송파구 가락동", "서울 종로구 궁정동"));
		List<MemberTown> memberTowns = MemberTown.create(regions, member);
		Member saveMember = memberRepository.save(member);
		memberTownRepository.saveAll(memberTowns);

		Principal principal = Principal.from(saveMember);
		Long addressId = getRegion("서울 종로구 청운동").getId();
		MemberTownAddRequest request = MemberTownAddRequest.create(addressId);
		// when
		Throwable throwable = catchThrowable(() -> memberTownService.addMemberTown(principal, request));
		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("회원이 가질 수 있는 개수(최대2개)를 초과하였습니다.");
	}

	@DisplayName("회원의 동네 추가시 이미 등록된 동네는 중복으로 추가할 수 없다")
	@Test
	public void addMemberTownWithDuplicateAddressName() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		MemberTown memberTown = MemberTown.create(getRegion("서울 종로구 신교동"), member);
		Member saveMember = memberRepository.save(member);
		memberTownRepository.save(memberTown);

		Principal principal = Principal.from(saveMember);
		MemberTownAddRequest request = MemberTownAddRequest.create(getRegion("서울 종로구 신교동").getId());
		// when
		Throwable throwable = catchThrowable(() -> memberTownService.addMemberTown(principal, request));
		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("이미 존재하는 동네입니다.");
	}

	@DisplayName("주소를 가지고 회원의 등록된 동네를 제거한다")
	@Test
	public void removeMemberTown() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		List<Region> regions = getRegions(List.of("서울 송파구 가락동", "서울 종로구 궁정동"));
		List<MemberTown> memberTowns = MemberTown.create(regions, member);
		Member saveMember = memberRepository.save(member);
		memberTownRepository.saveAll(memberTowns);

		Principal principal = Principal.from(saveMember);
		MemberTownRemoveRequest request = MemberTownRemoveRequest.create(getRegion("서울 송파구 가락동").getId());
		// when
		MemberTownRemoveResponse response = memberTownService.removeMemberTown(principal, request);
		// then
		assertAll(() -> {
			assertThat(response.getAddress()).isEqualTo("서울 송파구 가락동");
			assertThat(memberTownRepository.findMemberTownByMemberIdAndName(saveMember.getId(), "가락동")
				.isEmpty()).isTrue();
		});
	}

	@DisplayName("등록되지 않은 주소 이름을 가지고 회원의 동네를 제거할 수 없다")
	@Test
	public void removeMemberTownWithNotRegisteredAddressName() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		MemberTown memberTown = MemberTown.create(getRegion("서울 송파구 가락동"), member);
		Member saveMember = memberRepository.save(member);
		memberTownRepository.save(memberTown);

		Principal principal = Principal.from(saveMember);
		MemberTownRemoveRequest request = MemberTownRemoveRequest.create(getRegion("서울 종로구 효자동").getId());
		// when
		Throwable throwable = catchThrowable(() -> memberTownService.removeMemberTown(principal, request));
		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("등록되지 않은 동네를 삭제할 수 없습니다.");
	}

	@DisplayName("회원의 동네가 1개인 상태에서 회원의 동네를 제거할 수 없다")
	@Test
	public void removeMemberTownWithMinimumMemberTownSize() {
		// given
		Member member = OauthFixedFactory.createFixedMember();
		MemberTown memberTown = MemberTown.create(getRegion("서울 종로구 창성동"), member);
		Member saveMember = memberRepository.save(member);
		memberTownRepository.save(memberTown);

		Principal principal = Principal.from(saveMember);
		MemberTownRemoveRequest request = MemberTownRemoveRequest.create(getRegion("서울 종로구 창성동").getId());
		// when
		Throwable throwable = catchThrowable(() -> memberTownService.removeMemberTown(principal, request));
		// then
		Assertions.assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("동네는 최소 1개 이상 선택해야 해요. 새로운 동네를 등록한 후 삭제해주세요.");
	}

}
