package codesquard.app.api.oauth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codesquard.app.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@RestController
public class OauthRestController {

	private final OauthService oauthService;

	@PostMapping("/login")
	public ApiResponse<OauthLoginResponse> login() {
		return ApiResponse.ok(new OauthLoginResponse());
	}
}
