package codesquard.app.api.oauth;

import static org.springframework.http.HttpStatus.*;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.oauth.request.OauthLoginRequest;
import codesquard.app.api.oauth.request.OauthLogoutRequest;
import codesquard.app.api.oauth.request.OauthRefreshRequest;
import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthLoginResponse;
import codesquard.app.api.oauth.response.OauthRefreshResponse;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.config.ValidationSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class OauthRestController {

	private final OauthService oauthService;

	@ResponseStatus(CREATED)
	@PostMapping(value = "/{provider}/signup", consumes = {MediaType.APPLICATION_JSON_VALUE,
		MediaType.MULTIPART_FORM_DATA_VALUE})
	public ApiResponse<OauthSignUpResponse> signUp(
		@PathVariable String provider,
		@RequestParam String code,
		@RequestPart(value = "profile", required = false) MultipartFile profile,
		@Valid @RequestPart(value = "signupData") OauthSignUpRequest request) {
		log.info("provider : {}, code : {}, profile : {}, {}", provider, code, profile, request);

		oauthService.signUp(profile, request, provider, code);
		return ApiResponse.created("회원가입에 성공하였습니다.", null);
	}

	@PostMapping(value = "/{provider}/login")
	public ApiResponse<OauthLoginResponse> login(
		@PathVariable String provider,
		@RequestParam String code,
		@Validated(ValidationSequence.class) @RequestBody OauthLoginRequest request) {
		OauthLoginResponse response = oauthService.login(request, provider, code, LocalDateTime.now());
		return ApiResponse.of(OK, "로그인에 성공하였습니다.", response);
	}

	@PostMapping(value = "/logout")
	public ApiResponse<Void> logout(@RequestAttribute String accessToken,
		@RequestBody OauthLogoutRequest request) {
		log.info("로그아웃 요청 입력 : acessToken={}, request={}", accessToken, request);
		oauthService.logout(accessToken, request);
		return ApiResponse.ok("로그아웃에 성공하였습니다.", null);
	}

	@ResponseStatus(OK)
	@PostMapping("/token")
	public ApiResponse<OauthRefreshResponse> refreshAccessToken(@RequestBody OauthRefreshRequest request) {
		log.info("리프레시 토큰 API 요청 : {}", request);

		OauthRefreshResponse response = oauthService.refreshAccessToken(request, LocalDateTime.now());
		log.debug("리프레시 토큰 API 응답 : {}", response);
		return ApiResponse.ok("액세스 토큰 갱신에 성공하였습니다.", response);
	}

}
