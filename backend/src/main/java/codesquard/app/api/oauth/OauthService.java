package codesquard.app.api.oauth;

import org.springframework.stereotype.Service;

import codesquard.app.domain.jwt.Jwt;
import codesquard.app.domain.member.Member;

@Service
public class OauthService {

	public OauthLoginResponse login(OauthLoginRequest request, String code) {
		// 1. 인가 코드를 이용하여 Oauth 서버에 accessToken 발급
		// 2. accessToken을 이용하여 리소스 서버에 유저 프로필 정보 가져오기
		// 3. loginId와 socalLoginId가 각각의 정보와 일치하는지 조회하고 Member 객체 반환
		// 4. 인증된 회원 객체를 이용하여 JWT 객체 생성
		// 5. redis 서버에 refreshToken 갱신(저장 또는 업데이트)
		// 6. Response에 인증된 회원과 JWT 객체를 저장하여 반환
		return new OauthLoginResponse(new Jwt(), new Member());
	}
}
