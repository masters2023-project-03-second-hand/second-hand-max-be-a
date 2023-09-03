package codesquard.app.api.oauth;

import static org.springframework.http.HttpStatus.*;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import codesquard.app.domain.oauth.support.AuthPrincipal;
import codesquard.app.domain.oauth.support.Principal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class OauthRestController {

	private static final Logger log = LoggerFactory.getLogger(OauthRestController.class);

	private final OauthService oauthService;

	@ResponseStatus(CREATED)
	@PostMapping(value = "/{provider}/signup", consumes = {MediaType.APPLICATION_JSON_VALUE,
		MediaType.MULTIPART_FORM_DATA_VALUE})
	public ApiResponse<OauthSignUpResponse> signUp(
		@PathVariable String provider,
		@RequestParam String code,
		@RequestPart(value = "profile", required = false) MultipartFile profile,
		@Valid @RequestPart(value = "signupData") OauthSignUpRequest request) {
		log.info("provider : {}, code : {}, profile : {}, request : {}", provider, code, profile, request);

		oauthService.signUp(profile, request, provider, code);
		return ApiResponse.created("회원가입에 성공하였습니다.", null);
	}

	@ResponseStatus(OK)
	@PostMapping(value = "/{provider}/login")
	public ApiResponse<OauthLoginResponse> login(
		@PathVariable String provider,
		@RequestParam String code,
		@Valid @RequestBody OauthLoginRequest request) {
		OauthLoginResponse response = oauthService.login(request, provider, code, LocalDateTime.now());
		return ApiResponse.of(OK, "로그인에 성공하였습니다.", response);
	}

	@ResponseStatus(OK)
	@PostMapping(value = "/logout")
	public ApiResponse<Void> logout(@AuthPrincipal Principal principal) {
		log.info("principal : {}", principal);
		OauthLogoutRequest request = OauthLogoutRequest.create(principal);
		oauthService.logout(request);
		return ApiResponse.ok("로그아웃에 성공하였습니다.", null);
	}

	@ResponseStatus(OK)
	@PostMapping("/token")
	public ApiResponse<OauthRefreshResponse> refreshAccessToken(@AuthPrincipal Principal principal,
		@RequestBody OauthRefreshRequest request) {
		log.info("principal : {}, request : {}", principal, request);

		OauthRefreshResponse response = oauthService.refreshAccessToken(request, LocalDateTime.now());
		log.debug("response : {}", response);
		return ApiResponse.ok("액세스 토큰 갱신에 성공하였습니다.", response);
	}

}
