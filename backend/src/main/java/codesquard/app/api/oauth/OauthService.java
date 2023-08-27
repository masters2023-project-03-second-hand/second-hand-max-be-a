package codesquard.app.api.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquard.app.api.oauth.client.OauthClient;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthAccessTokenResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.oauth.response.OauthUserProfileResponse;
import codesquard.app.domain.member.Member;
import codesquard.app.domain.member.MemberRepository;
import codesquard.app.domain.oauth.OauthProvider;
import codesquard.app.domain.oauth.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class OauthService {

	private static final Logger log = LoggerFactory.getLogger(OauthService.class);

	private final ProviderRepository providerRepository;
	private final MemberRepository memberRepository;
	private final OauthClient oauthClient;

	public OauthSignUpResponse signUp(OauthSignUpRequest request, String provider, String authorizationCode) {
		log.info("OauthSignUpRequest : {}, provider : {}, authorizationCode : {}", request, provider,
			authorizationCode);

		// provider(naver, github, google...)등에 따른 oauth 정보를 가져온다
		OauthProvider oauthProvider = providerRepository.findByProviderName(provider);
		log.debug("oauthProvider : {}", oauthProvider);

		// authorizationCode를 가지고 Oauth 서버에 요청하여 accessToken을 발급받는다
		OauthAccessTokenResponse accessTokenResponse =
			oauthClient.exchangeAccessTokenByAuthorizationCode(oauthProvider, authorizationCode);
		log.debug("OauthAccessTokenResponse : {}", accessTokenResponse);

		// 발급받은 accessToken을 이용하여 유저 프로필 정보를 가져온다
		OauthUserProfileResponse userProfileResponse =
			oauthClient.getUserProfileByAccessToken(provider, oauthProvider, accessTokenResponse);
		log.debug("userProfileResponse : {}", userProfileResponse);

		// 회원 정보를 DB에 저장
		Member member = request.toEntity(userProfileResponse.getSocialLoginId());
		Member saveMember = memberRepository.save(member);

		return OauthSignUpResponse.of(saveMember);
	}
}
