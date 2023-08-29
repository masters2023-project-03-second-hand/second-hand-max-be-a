package codesquard.app.api.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.errors.errorcode.MemberErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.image.ImageService;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.oauth.client.OauthClient;
import codesquard.app.domain.oauth.repository.OauthClientRepository;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class OauthService {

	private static final Logger log = LoggerFactory.getLogger(OauthService.class);

	private final OauthClientRepository oauthClientRepository;
	private final MemberRepository memberRepository;
	private final ImageService imageService;

	public OauthSignUpResponse signUp(MultipartFile profile, OauthSignUpRequest request, String provider,
		String authorizationCode) {
		log.info("OauthSignUpRequest : {}, provider : {}, authorizationCode : {}", request, provider,
			authorizationCode);

		// provider(naver, github, google...)등에 따른 oauth 정보를 가져온다
		OauthClient oauthClient = oauthClientRepository.findOneBy(provider);
		log.debug("oauthProvider : {}", oauthClient);

		// authorizationCode를 가지고 Oauth 서버에 요청하여 accessToken을 발급받는다
		OauthAccessTokenResponse accessTokenResponse =
			oauthClient.exchangeAccessTokenByAuthorizationCode(authorizationCode);
		log.debug("OauthAccessTokenResponse : {}", accessTokenResponse);

		// 발급받은 accessToken을 이용하여 유저 프로필 정보를 가져온다
		OauthUserProfileResponse userProfileResponse =
			oauthClient.getUserProfileByAccessToken(provider, accessTokenResponse);
		log.debug("userProfileResponse : {}", userProfileResponse);

		// 프로필 사진 업로드
		String avatarUrl = null;
		if (profile != null) {
			avatarUrl = imageService.uploadImage(profile);
			log.debug("avatarUrl : {}", avatarUrl);
		}

		Member member = request.toEntity(avatarUrl, userProfileResponse.getEmail());

		// 중복 로그인 아이디 검증
		validateDuplicateLoginId(member.getLoginId());

		// 회원 저장
		Member saveMember = memberRepository.save(member);

		return OauthSignUpResponse.from(saveMember);
	}

	private void validateDuplicateLoginId(String loginId) {
		if (memberRepository.existsMemberByLoginId(loginId)) {
			throw new RestApiException(MemberErrorCode.ALREADY_EXIST_ID);
		}
	}
}
