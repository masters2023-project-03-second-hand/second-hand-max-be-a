package codesquard.app;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import codesquard.app.api.oauth.OauthRestController;
import codesquard.app.api.oauth.OauthService;

@WebMvcTest(
	OauthRestController.class
)
public abstract class ControllerTestSupport {
	@MockBean
	protected OauthService oauthService;
}
