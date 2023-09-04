package codesquard.app.api.oauth;

import java.time.LocalDateTime;
import java.util.Objects;
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
import codesquard.app.api.oauth.response.OauthLogoutResponse;
import codesquard.app.api.oauth.response.OauthRefreshResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class OauthService {

	private final OauthClientRepository oauthClientRepository;
	private final MemberRepository memberRepository;
	private final ImageService imageService;
	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, Object> redisTemplate;

	public OauthSignUpResponse signUp(MultipartFile profile, OauthSignUpRequest request, String provider,
		String authorizationCode) {
		log.info("{}, provider : {}, authorizationCode : {}", request, provider,
			authorizationCode);

		// 중복 로그인 아이디 검증
		validateDuplicateLoginId(request.getLoginId());

		// 액세스 토큰 발급 및 유저 정보 가져오기
		OauthUserProfileResponse userProfileResponse = getOauthUserProfileResponse(provider, authorizationCode);

		// 프로필 사진 업로드
		String avatarUrl = null;
		if (profile == null) {
			avatarUrl = userProfileResponse.getProfileImage();
		} else {
			avatarUrl = imageService.uploadImage(profile);
		}
		log.debug(avatarUrl);

		Member member = request.toEntity(avatarUrl, userProfileResponse.getEmail());

		// 회원 저장
		Member saveMember = memberRepository.save(member);

		return OauthSignUpResponse.from(saveMember);
	}

	private OauthUserProfileResponse getOauthUserProfileResponse(String provider, String authorizationCode) {
		// provider(naver, github, google...)등에 따른 oauth 정보를 가져온다
		OauthClient oauthClient = oauthClientRepository.findOneBy(provider);

		// authorizationCode를 가지고 Oauth 서버에 요청하여 accessToken을 발급받는다
		OauthAccessTokenResponse accessTokenResponse =
			oauthClient.exchangeAccessTokenByAuthorizationCode(authorizationCode);
		log.debug("{}", accessTokenResponse);

		// 발급받은 accessToken을 이용하여 유저 프로필 정보를 가져온다
		OauthUserProfileResponse userProfileResponse =
			oauthClient.getUserProfileByAccessToken(accessTokenResponse);
		log.debug("{}", userProfileResponse);
		return userProfileResponse;
	}

	private void validateDuplicateLoginId(String loginId) {
		if (memberRepository.existsMemberByLoginId(loginId)) {
			throw new RestApiException(MemberErrorCode.ALREADY_EXIST_ID);
		}
	}

	public OauthLoginResponse login(OauthLoginRequest request, String provider, String code, LocalDateTime now) {
		log.info("{}, provider : {}, code : {}", request, provider, code);

		OauthUserProfileResponse userProfileResponse = getOauthUserProfileResponse(provider, code);

		// 로그인 아이디와 이메일에 따른 회원 조회
		Member member = getLoginMember(request, userProfileResponse);
		log.debug("{}", member);

		// JWT 객체 생성
		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);
		log.debug("{}", jwt);

		// 리프레쉬 토큰 저장
		// key: "RT:" + email, value : 리프레쉬 토큰값
		redisTemplate.opsForValue().set(member.createRedisKey(),
			jwt.getRefreshToken(),
			jwt.getExpireDateRefreshTokenTime(),
			TimeUnit.MILLISECONDS);

		return OauthLoginResponse.create(jwt, OauthLoginMemberResponse.from(member));
	}

	private Member getLoginMember(OauthLoginRequest request, OauthUserProfileResponse userProfileResponse) {
		String loginId = request.getLoginId();
		String email = userProfileResponse.getEmail();
		return memberRepository.findMemberByLoginIdAndEmail(loginId, email)
			.orElseThrow(() -> new RestApiException(OauthErrorCode.FAIL_LOGIN));
	}

	public OauthLogoutResponse logout(OauthLogoutRequest request) {
		log.info("{}", request);
		Principal principal = request.getPrincipal();

		// Redis에 유저 email로 저장된 RefreshToken이 있는지 확인
		if (redisTemplate.opsForValue().get(principal.createRedisKey()) != null) {
			// RefreshToken 삭제
			redisTemplate.delete(principal.createRedisKey());
		}
		// 해당 액세스 토큰 유효시간을 가지고 와서 블랙리스트에 저장하기
		long expiration = request.getPrincipal().getExpireDateAccessToken();
		redisTemplate.opsForValue().set(principal.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
		return OauthLogoutResponse.from(principal);
	}

	public OauthRefreshResponse refreshAccessToken(OauthRefreshRequest request, LocalDateTime now) {
		String refreshToken = request.getRefreshToken();

		// 토큰이 유효한지 검증합니다.
		jwtProvider.validateToken(refreshToken);
		log.debug("refreshToken is valid token : {}", refreshToken);

		// 리프레쉬 토큰을 가지고 이메일 조회
		String email = findEmailByRefreshToken(refreshToken);
		Member member = memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		log.debug("{}", member);

		// jwt 객체 생성
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
			.orElseThrow(() -> new RestApiException(JwtTokenErrorCode.INVALID_TOKEN));
	}
}
