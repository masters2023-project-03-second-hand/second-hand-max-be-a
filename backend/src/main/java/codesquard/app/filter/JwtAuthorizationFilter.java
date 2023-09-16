package codesquard.app.filter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.errors.errorcode.ErrorCode;
import codesquard.app.api.errors.errorcode.JwtTokenErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.redis.RedisService;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.oauth.support.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer";
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();
	private static final List<String> excludeUrlPatterns = List.of(
		"/api/auth/**/signup",
		"/api/auth/**/login",
		"/api/auth/token",
		"/api/auth/logout",
		"/api/categories");
	private final JwtProvider jwtProvider;
	private final AuthenticationContext authenticationContext;
	private final ObjectMapper objectMapper;
	private final RedisService redisService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		if (pathMatcher.match("/api/regions", request.getRequestURI())
			&& Objects.equals("GET", request.getMethod())) {
			return true;
		}
		if (pathMatcher.match("/api/items", request.getRequestURI())
			&& Objects.equals("GET", request.getMethod())) {
			return true;
		}
		return excludeUrlPatterns.stream()
			.anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		if (CorsUtils.isPreFlightRequest(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		try {
			String token = extractJwt(request).orElseThrow(() -> new RestApiException(JwtTokenErrorCode.EMPTY_TOKEN));
			jwtProvider.validateToken(token);
			redisService.validateAlreadyLogout(token);
			authenticationContext.setPrincipal(jwtProvider.extractPrincipal(token));
		} catch (RestApiException e) {
			setErrorResponse(response, e.getErrorCode());
			return;
		}

		filterChain.doFilter(request, response);
	}

	private Optional<String> extractJwt(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION);

		if (!StringUtils.hasText(header) || !header.startsWith(BEARER)) {
			return Optional.empty();
		}

		return Optional.of(header.split(" ")[1]);
	}

	private void setErrorResponse(HttpServletResponse httpServletResponse, ErrorCode errorCode) throws IOException {
		httpServletResponse.setStatus(errorCode.getHttpStatus().value());
		httpServletResponse.setContentType("application/json");
		httpServletResponse.setCharacterEncoding("UTF-8");
		ApiResponse<Object> body = ApiResponse.error(errorCode);
		httpServletResponse.getWriter().write(objectMapper.writeValueAsString(body));
	}
}
