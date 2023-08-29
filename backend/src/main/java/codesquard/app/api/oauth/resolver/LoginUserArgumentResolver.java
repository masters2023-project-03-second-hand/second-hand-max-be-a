package codesquard.app.api.oauth.resolver;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import codesquard.app.domain.member.AuthenticateMember;
import codesquard.app.filter.JwtAuthorizationFilter;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
	private static final Logger log = LoggerFactory.getLogger(LoginUserArgumentResolver.class);

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		log.debug("supportParameter 실행");
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean hasUserType = AuthenticateMember.class.isAssignableFrom(parameter.getParameterType());

		return hasLoginAnnotation && hasUserType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		AuthenticateMember authenticateMember = (AuthenticateMember)request.getAttribute(
			JwtAuthorizationFilter.AUTHENTICATE_MEMBER);
		log.debug("resolveArgument, authenticateUser : {}", authenticateMember);
		return authenticateMember;
	}
}
