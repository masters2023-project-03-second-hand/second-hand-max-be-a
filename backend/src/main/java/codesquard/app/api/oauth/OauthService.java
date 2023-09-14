package codesquard.app.api.oauth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.errors.errorcode.JwtTokenErrorCode;
import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.image.ImageService;
import codesquard.app.api.oauth.request.OauthLoginRequest;
import codesquard.app.api.oauth.request.OauthLogoutRequest;
import codesquard.app.api.oauth.request.OauthRefreshRequest;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthLoginMemberResponse;
import codesquard.app.api.oauth.response.OauthLoginResponse;
import codesquard.app.api.oauth.response.OauthRefreshResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.membertown.MemberTown;
import codesquard.app.domain.membertown.MemberTownRepository;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;
import codesquard.app.domain.region.Region;
import codesquard.app.domain.region.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class OauthService {

	private final OauthClientRepository oauthClientRepository;
	private final MemberRepository memberRepository;
	private final MemberTownRepository memberTownRepository;
	private final RegionRepository regionRepository;
	private final ImageService imageService;
	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, Object> redisTemplate;

	public OauthSignUpResponse signUp(MultipartFile profile, OauthSignUpRequest request, String provider,
		String authorizationCode) {
		log.info("{}, provider : {}, authorizationCode : {}", request, provider,
			authorizationCode);
		Optional<MultipartFile> optionalProfile = Optional.ofNullable(profile);

		validateDuplicateLoginId(request.getLoginId());

		OauthUserProfileResponse userProfileResponse = getOauthUserProfileResponse(provider, authorizationCode);

		validateMultipleSignUp(userProfileResponse.getEmail());

		String avatarUrl = optionalProfile.map(imageService::uploadImage)
			.orElse(userProfileResponse.getProfileImage());
		log.debug("회원 가입 서비스에서 생성한 아바타 주소 : {}", avatarUrl);

		Member member = request.toEntity(avatarUrl, userProfileResponse.getEmail());
		Member saveMember = memberRepository.save(member);
		log.debug("회원 엔티티 저장 결과 : {}", saveMember);

		List<Region> regions = regionRepository.findAllById(request.getAddressIds());
		List<MemberTown> memberTowns = MemberTown.create(regions, member);
		memberTownRepository.saveAll(memberTowns);
		log.debug("회원 동네 저장 결과 : {}", memberTowns);

		return OauthSignUpResponse.from(saveMember);
	}

	private void validateDuplicateLoginId(String loginId) {
		if (memberRepository.existsMemberByLoginId(loginId)) {
			throw new RestApiException(MemberErrorCode.ALREADY_EXIST_ID);
		}
	}

	private void validateMultipleSignUp(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			throw new RestApiException(OauthErrorCode.ALREADY_SIGNUP);
		}
	}

	private OauthUserProfileResponse getOauthUserProfileResponse(String provider, String authorizationCode) {
		OauthClient oauthClient = oauthClientRepository.findOneBy(provider);

		OauthAccessTokenResponse accessTokenResponse =
			oauthClient.exchangeAccessTokenByAuthorizationCode(authorizationCode);
		log.debug("{}", accessTokenResponse);

		OauthUserProfileResponse userProfileResponse =
			oauthClient.getUserProfileByAccessToken(accessTokenResponse);
		log.debug("{}", userProfileResponse);
		return userProfileResponse;
	}

	public OauthLoginResponse login(OauthLoginRequest request, String provider, String code, LocalDateTime now) {
		log.info("{}, provider : {}, code : {}", request, provider, code);

		OauthUserProfileResponse userProfileResponse = getOauthUserProfileResponse(provider, code);

		Member member = getLoginMember(request, userProfileResponse);
		log.debug("로그인 서비스 요청 중 회원 객체 생성 : {}", member);

		List<MemberTown> memberTowns = memberTownRepository.findAllByMemberId(member.getId());
		log.debug("로그인 서비스 요청 중 회원 동네 조회 : {}", memberTowns);

		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);
		log.debug("로그인 서비스 요청 중 jwt 객체 생성 : {}", jwt);

		// key: "RT:" + email, value : 리프레쉬 토큰값
		redisTemplate.opsForValue().set(member.createRedisKey(),
			jwt.getRefreshToken(),
			jwt.convertExpireDateRefreshTokenTimeWithLong(),
			TimeUnit.MILLISECONDS);

		return OauthLoginResponse.create(jwt, OauthLoginMemberResponse.from(member, memberTowns));
	}

	private Member getLoginMember(OauthLoginRequest request, OauthUserProfileResponse userProfileResponse) {
		String loginId = request.getLoginId();
		String email = userProfileResponse.getEmail();
		return memberRepository.findMemberByLoginIdAndEmail(loginId, email)
			.orElseThrow(() -> new RestApiException(OauthErrorCode.FAIL_LOGIN));
	}

	public void logout(OauthLogoutRequest request) {
		log.info("로그아웃 서비스 요청 : {}", request);
		String accessToken = request.getAccessToken();
		String refreshToken = request.getRefreshToken();

		deleteRefreshTokenBy(refreshToken);
		registerAccessTokenToBlackList(accessToken);
	}

	private void deleteRefreshTokenBy(String refreshToken) {
		String email = findEmailByRefreshToken(refreshToken);
		redisTemplate.delete(String.format("RT:%s", email));
	}

	private void registerAccessTokenToBlackList(String accessToken) {
		try {
			long expiration = ((Integer)jwtProvider.getClaims(accessToken).get("exp")).longValue();
			redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
		} catch (RestApiException e) {
			log.error("액세스 토큰 에러 : {}", e.getMessage());
		}
	}

	public OauthRefreshResponse refreshAccessToken(OauthRefreshRequest request, LocalDateTime now) {
		String refreshToken = request.getRefreshToken();

		jwtProvider.validateToken(refreshToken);
		log.debug("refreshToken is valid token : {}", refreshToken);

		String email = findEmailByRefreshToken(refreshToken);
		log.debug("findEmailByRefreshToken 결과 : email={}", email);
		Member member = memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		log.debug("findMemberByEmail 결과 : member={}", member);

		Jwt jwt = jwtProvider.createJwtWithRefreshTokenBasedOnMember(member, refreshToken, now);

		return OauthRefreshResponse.create(jwt);
	}

	private String findEmailByRefreshToken(String refreshToken) {
		Set<String> keys = redisTemplate.keys("RT:*");
		if (keys == null) {
			throw new RestApiException(JwtTokenErrorCode.EMPTY_TOKEN);
		}
		return keys.stream()
			.filter(key -> Objects.equals(redisTemplate.opsForValue().get(key), refreshToken))
			.findAny()
			.map(email -> email.replace("RT:", ""))
			.orElse(null);
	}
}
