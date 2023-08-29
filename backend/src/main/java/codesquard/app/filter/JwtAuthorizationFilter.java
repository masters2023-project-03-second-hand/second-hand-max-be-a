package codesquard.app.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.PatternMatchUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.errors.errorcode.ErrorCode;
import codesquard.app.api.errors.errorcode.JwtTokenErrorCode;
import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.api.response.ApiResponse;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.member.AuthenticateMember;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtAuthorizationFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

	private static final String[] whiteListUris = {"/api/auth/naver/signup", "/api/auth/naver/login"};

	public static final String AUTHENTICATE_MEMBER = "authMember";

	private final JwtProvider jwtProvider;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;

	public JwtAuthorizationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper,
		RedisTemplate<String, Object> redisTemplate) {
		this.jwtProvider = jwtProvider;
		this.objectMapper = objectMapper;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		ServletException,
		IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;

		log.info("doFilter : {}", httpServletRequest.getRequestURI());

		if (whiteListCheck(httpServletRequest.getRequestURI())) {
			chain.doFilter(request, response);
			return;
		}
		log.debug("whiteListCheck pass : {}", httpServletRequest.getRequestURI());

		if (!isContainToken(httpServletRequest)) {
			setErrorResponse(httpServletResponse, JwtTokenErrorCode.FAIL_AUTHENTICATION);
			return;
		}
		log.debug("contains accessToken");

		try {
			// 액세스 토큰값 추출
			String token = parseToken(httpServletRequest);
			log.debug("token : {}", token);

			// Redis에 해당 액세스토큰 로그아웃 여부를 확인
			// logout이 "logout"이 아닌 경우 인가 절차를 계속 진행합니다.
			String logout = (String)redisTemplate.opsForValue().get(token);
			log.debug("logout : {}", logout);
			validateIsLogout(logout);

			// 액세스 토큰 복호화하여 AuthenticateUser 객체로 읽기
			AuthenticateMember authenticateMember = getAuthenticateUser(token);
			log.debug("authenticateMember : {}", authenticateMember);

			// Request 속성에 AuthenticateUser 객체 저장
			httpServletRequest.setAttribute(AUTHENTICATE_MEMBER, authenticateMember);

			chain.doFilter(request, response);
		} catch (JsonParseException e) {
			log.error("JsonParseException : {}", e.getMessage());
			setErrorResponse(httpServletResponse, JwtTokenErrorCode.FAIL_PARSE_JSON);
		} catch (MalformedJwtException | UnsupportedJwtException e) {
			log.error("JwtException : {}", e.getMessage());
			setErrorResponse(httpServletResponse, JwtTokenErrorCode.FAIL_AUTHENTICATION);
		} catch (ExpiredJwtException e) {
			log.error("ExpiredJwtException : {}", e.getMessage());
			setErrorResponse(httpServletResponse, JwtTokenErrorCode.EXPIRE_TOKEN);
		} catch (RestApiException e) {
			log.error("RestApiException : {}", e.getMessage());
			setErrorResponse(httpServletResponse, e.getErrorCode());
		}
	}

	private void setErrorResponse(HttpServletResponse httpServletResponse, ErrorCode errorCode) throws IOException {
		httpServletResponse.setStatus(errorCode.getHttpStatus().value());
		httpServletResponse.setContentType("application/json");
		httpServletResponse.setCharacterEncoding("UTF-8");
		ApiResponse<Object> body = ApiResponse.error(errorCode);
		httpServletResponse.getWriter().write(objectMapper.writeValueAsString(body));
	}

	// 요청한 URI가 화이트리스트에 있는지 확인합니다. 화이트 리스트에 있으면 인가처리합니다.
	private boolean whiteListCheck(String requestUrl) {
		return PatternMatchUtils.simpleMatch(whiteListUris, requestUrl);
	}

	// 요청 헤더에 Authorization 헤더가 있고 "Bearer "로 시작하는지 확인합니다.
	private boolean isContainToken(HttpServletRequest httpServletRequest) {
		String authorization = httpServletRequest.getHeader("Authorization");
		return authorization != null && authorization.startsWith("Bearer ");
	}

	// Authorization 헤더에서 액세스 토큰 값을 추출하여 반환합니다.
	private String parseToken(HttpServletRequest httpServletRequest) {
		String authorization = httpServletRequest.getHeader("Authorization");
		return authorization.substring(7);
	}

	// 토큰을 비밀키로 복호화하고 복호화한 데이터를 기반으로 AuthenticateUser 객체로 읽습니다.
	private AuthenticateMember getAuthenticateUser(String token) throws JsonProcessingException {
		Claims claims = jwtProvider.getClaims(token);
		String authenticateUserJson = claims.get(AUTHENTICATE_MEMBER, String.class);
		return objectMapper.readValue(authenticateUserJson, AuthenticateMember.class);
	}

	private void validateIsLogout(String logout) {
		if (Objects.equals(logout, "logout")) {
			throw new RestApiException(OauthErrorCode.ALREADY_LOGOUT);
		}
	}
}
