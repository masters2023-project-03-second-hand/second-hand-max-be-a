package codesquard.app.api.membertown;

import static codesquard.app.MemberTestSupport.*;
import static codesquard.app.RegionTestSupport.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionRepository;

@SpringBootTest
class MemberTownServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberTownRepository memberTownRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private MemberTownService memberTownService;

	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach
	void tearDown() {
		memberTownRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("선택한 동네를 회원 동네에 추가한다")
	@Test
	public void addMemberTown() throws JsonProcessingException {
		// given
		Region region = regionRepository.save(createRegion("서울 송파구 가락동"));

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", region.getId());
		MemberTownAddRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			MemberTownAddRequest.class);

		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		Member saveMember = memberRepository.save(member);

		// when
		MemberAddRegionResponse response = memberTownService.addMemberTown(Principal.from(saveMember), request);

		// then
		List<MemberTown> memberTowns = memberTownRepository.findAll();
		assertAll(() -> {
			assertThat(response.getName()).isEqualTo("가락동");
			assertThat(memberTowns).hasSize(1);
		});
	}

	@DisplayName("주소에 없는 동네를 회원 동네에 추가할 수 없다")
	@Test
	public void addMemberTownWithNotExistFullAddressName() throws JsonProcessingException {
		// given
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", 9999L);
		MemberTownAddRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			MemberTownAddRequest.class);

		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		Member saveMember = memberRepository.save(member);

		// when
		Throwable throwable = catchThrowable(
			() -> memberTownService.addMemberTown(Principal.from(saveMember), request));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("주소를 찾지 못하였습니다.");
	}

	@DisplayName("회원의 동네 추가시 동네 등록 최대개수를 초과하여서 회원의 동네를 추가할 수 없다")
	@Test
	public void addMemberTownWithOverTheMaximumMemberTownSize() throws JsonProcessingException {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		Member saveMember = memberRepository.save(member);

		List<Region> regions = regionRepository.saveAll(
			createRegions(List.of("서울 송파구 가락동", "서울 종로구 궁정동", "서울 종로구 효자동")));
		List<MemberTown> memberTowns = MemberTown.createMemberTowns(regions, member);
		memberTownRepository.saveAll(memberTowns);

		Region region = regionRepository.save(createRegion("서울 종로구 청운동"));
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", region.getId());
		MemberTownAddRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			MemberTownAddRequest.class);

		// when
		Throwable throwable = catchThrowable(
			() -> memberTownService.addMemberTown(Principal.from(saveMember), request));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("회원이 가질 수 있는 개수(최대2개)를 초과하였습니다.");
	}

	@DisplayName("회원의 동네 추가시 이미 등록된 동네는 중복으로 추가할 수 없다")
	@Test
	public void addMemberTownWithDuplicateAddressName() throws JsonProcessingException {
		// given
		Map<String, Object> requestBody = new HashMap<>();
		Region region = regionRepository.save(createRegion("서울 종로구 신교동"));
		requestBody.put("addressId", region.getId());
		MemberTownAddRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			MemberTownAddRequest.class);

		Member member = memberRepository.save(createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong"));
		memberTownRepository.save(new MemberTown(region.getShortAddress(), member, region));

		// when
		Throwable throwable = catchThrowable(
			() -> memberTownService.addMemberTown(Principal.from(member), request));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("이미 존재하는 동네입니다.");
	}

	@DisplayName("주소를 가지고 회원의 등록된 동네를 제거한다")
	@Test
	public void removeMemberTown() throws JsonProcessingException {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		Member saveMember = memberRepository.save(member);

		List<Region> regions = regionRepository.saveAll(createRegions(List.of("서울 송파구 가락동", "서울 종로구 궁정동")));
		List<MemberTown> memberTowns = MemberTown.createMemberTowns(regions, member);
		memberTownRepository.saveAll(memberTowns);

		Region requestRegion = regions.stream()
			.filter(region -> region.getName().equals("서울 송파구 가락동"))
			.findAny()
			.orElseThrow();
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", requestRegion.getId());
		MemberTownRemoveRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			MemberTownRemoveRequest.class);

		// when
		MemberTownRemoveResponse response = memberTownService.removeMemberTown(Principal.from(saveMember), request);

		// then
		assertAll(() -> {
			assertThat(response.getAddress()).isEqualTo("서울 송파구 가락동");
			assertThat(memberTownRepository.findMemberTownByMemberIdAndName(saveMember.getId(), "가락동")
				.isEmpty()).isTrue();
		});
	}

	@DisplayName("등록되지 않은 주소 이름을 가지고 회원의 동네를 제거할 수 없다")
	@Test
	public void removeMemberTownWithNotRegisteredAddressName() throws JsonProcessingException {
		// given
		Region region = regionRepository.save(createRegion("서울 종로구 효자동"));

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", region.getId());
		MemberTownRemoveRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			MemberTownRemoveRequest.class);

		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		memberRepository.save(member);

		Region newRegion = regionRepository.save(createRegion("서울 송파구 가락동"));
		MemberTown memberTown = new MemberTown(newRegion.getShortAddress(), member, newRegion);
		memberTownRepository.save(memberTown);

		// when
		Throwable throwable = catchThrowable(() -> memberTownService.removeMemberTown(Principal.from(member), request));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("등록되지 않은 동네를 삭제할 수 없습니다.");
	}

	@DisplayName("회원의 동네가 1개인 상태에서 회원의 동네를 제거할 수 없다")
	@Test
	public void removeMemberTownWithMinimumMemberTownSize() throws JsonProcessingException {
		// given
		Member member = createMember("avatarUrlValue", "23Yong@gmail.com", "23Yong");
		Member saveMember = memberRepository.save(member);

		Region region = regionRepository.save(createRegion("서울 종로구 창성동"));
		MemberTown memberTown = new MemberTown(region.getShortAddress(), member, region);
		memberTownRepository.save(memberTown);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("addressId", region.getId());
		MemberTownRemoveRequest request = objectMapper.readValue(objectMapper.writeValueAsString(requestBody),
			MemberTownRemoveRequest.class);

		// when
		Throwable throwable = catchThrowable(
			() -> memberTownService.removeMemberTown(Principal.from(saveMember), request));

		// then
		assertThat(throwable)
			.isInstanceOf(RestApiException.class)
			.extracting("errorCode.message")
			.isEqualTo("동네는 최소 1개 이상 선택해야 해요. 새로운 동네를 등록한 후 삭제해주세요.");
	}
}
