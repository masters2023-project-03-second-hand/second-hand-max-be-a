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

		validateDuplicateLoginId(request.getLoginId());

		OauthUserProfileResponse userProfileResponse = getOauthUserProfileResponse(provider, authorizationCode);

		String avatarUrl = null;
		if (profile == null) {
			avatarUrl = userProfileResponse.getProfileImage();
		} else {
			avatarUrl = imageService.uploadImage(profile);
		}
		log.debug(avatarUrl);

		Member member = request.toEntity(avatarUrl, userProfileResponse.getEmail());

		Member saveMember = memberRepository.save(member);

		return OauthSignUpResponse.from(saveMember);
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

	private void validateDuplicateLoginId(String loginId) {
		if (memberRepository.existsMemberByLoginId(loginId)) {
			throw new RestApiException(MemberErrorCode.ALREADY_EXIST_ID);
		}
	}

	public OauthLoginResponse login(OauthLoginRequest request, String provider, String code, LocalDateTime now) {
		log.info("{}, provider : {}, code : {}", request, provider, code);

		OauthUserProfileResponse userProfileResponse = getOauthUserProfileResponse(provider, code);

		Member member = getLoginMember(request, userProfileResponse);
		log.debug("{}", member);

		Jwt jwt = jwtProvider.createJwtBasedOnMember(member, now);
		log.debug("{}", jwt);

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

		jwtProvider.validateToken(refreshToken);
		log.debug("refreshToken is valid token : {}", refreshToken);

		String email = findEmailByRefreshToken(refreshToken);
		Member member = memberRepository.findMemberByEmail(email)
			.orElseThrow(() -> new RestApiException(MemberErrorCode.NOT_FOUND_MEMBER));
		log.debug("{}", member);

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
