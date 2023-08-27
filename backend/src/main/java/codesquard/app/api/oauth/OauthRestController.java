package codesquard.app.api.oauth;

import static org.springframework.http.HttpStatus.*;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import codesquard.app.api.oauth.request.OauthSignUpRequest;
import codesquard.app.api.oauth.response.OauthSignUpResponse;
import codesquard.app.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class OauthRestController {

	private static final Logger log = LoggerFactory.getLogger(OauthRestController.class);

	private final OauthService oauthService;

	@PostMapping("/{provider}/signup")
	public ResponseEntity<ApiResponse<OauthSignUpResponse>> signUp(
		@PathVariable(value = "provider") String provider,
		@RequestParam String code,
		@RequestPart(value = "profile", required = false) MultipartFile profile,
		@Valid @RequestPart(value = "signupData") OauthSignUpRequest request) {
		log.info("provider : {}, code : {}, profile : {}, request : {}", provider, code, profile, request);

		OauthSignUpResponse response = oauthService.signUp(request, provider, code);
		return ResponseEntity.status(CREATED)
			.body(ApiResponse.created(response));
	}
}
