package codesquard.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.oauth.OauthRestController;
import codesquard.app.api.oauth.OauthService;

@WebMvcTest(
	OauthRestController.class
)
public abstract class ControllerTestSupport {

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected OauthService oauthService;
}
