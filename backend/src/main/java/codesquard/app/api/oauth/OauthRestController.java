package codesquard.app.api.oauth;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class OauthRestController {

	private final OauthService oauthService;

	@PostMapping("/login")
	public ApiResponse<OauthLoginResponse> login(@Valid @RequestBody OauthLoginRequest request,
		@RequestParam String code) {
		OauthLoginResponse response = oauthService.login(request, code);
		return ApiResponse.ok(response);
	}
}
