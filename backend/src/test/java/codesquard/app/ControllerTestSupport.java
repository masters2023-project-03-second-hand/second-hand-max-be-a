package codesquard.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.errors.handler.GlobalExceptionHandler;
import codesquard.app.api.redis.ItemViewRedisService;
import codesquard.app.api.redis.OauthRedisService;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.oauth.support.AuthPrincipalArgumentResolver;
import codesquard.app.domain.oauth.support.AuthenticationContext;

public abstract class ControllerTestSupport {

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected GlobalExceptionHandler globalExceptionHandler;

	@MockBean
	protected JwtProvider jwtProvider;

	@MockBean
	protected RedisTemplate<String, Object> redisTemplate;

	@MockBean
	protected OauthRedisService oauthRedisService;

	@MockBean
	protected ItemViewRedisService itemViewRedisService;

	@MockBean
	protected AuthenticationContext authenticationContext;

	@MockBean
	protected AuthPrincipalArgumentResolver authPrincipalArgumentResolver;
}
