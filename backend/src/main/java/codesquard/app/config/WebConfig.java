package codesquard.app.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.oauth.support.AuthPrincipalArgumentResolver;
import codesquard.app.domain.oauth.support.AuthenticationContext;
import codesquard.app.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final AuthPrincipalArgumentResolver authPrincipalArgumentResolver;

	private final JwtProvider jwtProvider;

	private final AuthenticationContext authenticationContext;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authPrincipalArgumentResolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}
	
	@Bean
	public FilterRegistrationBean<JwtAuthorizationFilter> jwtAuthorizationFilter() {
		FilterRegistrationBean<JwtAuthorizationFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
		filterFilterRegistrationBean.setFilter(new JwtAuthorizationFilter(jwtProvider, authenticationContext));
		filterFilterRegistrationBean.addUrlPatterns("/api/*");
		return filterFilterRegistrationBean;
	}
}
