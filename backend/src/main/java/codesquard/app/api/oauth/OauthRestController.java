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
import codesquard.app.api.success.successcode.OauthSuccessCode;
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
		@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
		@RequestPart(value = "profile", required = false) MultipartFile profile,
		@Valid @RequestPart(value = "signupData") OauthSignUpRequest request) {
		log.info("provider : {}, code : {}, requestUrl : {}, profile : {}, request : {}", provider, code, redirectUrl,
			profile, request);

		oauthService.signUp(profile, request, provider, code, redirectUrl);
		return ApiResponse.success(OauthSuccessCode.CREATED_SIGNUP);
	}

	@PostMapping(value = "/{provider}/login")
	public ApiResponse<OauthLoginResponse> login(
		@PathVariable String provider,
		@RequestParam String code,
		@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
		@Validated(ValidationSequence.class) @RequestBody OauthLoginRequest request) {
		OauthLoginResponse response = oauthService.login(request, provider, code, LocalDateTime.now(), redirectUrl);
		return ApiResponse.success(OauthSuccessCode.OK_LOGIN, response);
	}

	@PostMapping(value = "/logout")
	public ApiResponse<Void> logout(@RequestAttribute String accessToken,
		@RequestBody OauthLogoutRequest request) {
		log.info("로그아웃 요청 입력 : acessToken={}, request={}", accessToken, request);
		oauthService.logout(accessToken, request);
		return ApiResponse.success(OauthSuccessCode.OK_LOGOUT);
	}

	@ResponseStatus(OK)
	@PostMapping("/token")
	public ApiResponse<OauthRefreshResponse> refreshAccessToken(@RequestBody OauthRefreshRequest request) {
		log.info("리프레시 토큰 API 요청 : {}", request);

		OauthRefreshResponse response = oauthService.refreshAccessToken(request, LocalDateTime.now());
		log.debug("리프레시 토큰 API 응답 : {}", response);
		return ApiResponse.success(OauthSuccessCode.OK_REFRESH_TOKEN, response);
	}

}
