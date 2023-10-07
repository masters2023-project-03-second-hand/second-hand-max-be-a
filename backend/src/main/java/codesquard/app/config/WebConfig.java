package codesquard.app.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import codesquard.app.api.converter.SalesRequestConverter;
import codesquard.app.api.converter.WishRequestConverter;
import codesquard.app.api.redis.OauthRedisService;
import codesquard.app.domain.jwt.JwtProvider;
import codesquard.app.domain.oauth.support.AuthPrincipalArgumentResolver;
import codesquard.app.domain.oauth.support.AuthenticationContext;
import codesquard.app.filter.JwtAuthorizationFilter;
import codesquard.app.filter.LogoutFilter;
import codesquard.app.interceptor.LogoutInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final int CACHE_MAX_AGES_SECONDS = 3600;

	private final AuthPrincipalArgumentResolver authPrincipalArgumentResolver;
	private final JwtProvider jwtProvider;
	private final AuthenticationContext authenticationContext;
	private final OauthRedisService redisService;
	private final ObjectMapper objectMapper;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LogoutInterceptor())
			.excludePathPatterns("/api/*")
			.addPathPatterns("/api/auth/logout");

		WebContentInterceptor webCacheContentInterceptor = new WebContentInterceptor();
		webCacheContentInterceptor.addCacheMapping(
			CacheControl.maxAge(CACHE_MAX_AGES_SECONDS, TimeUnit.SECONDS).noTransform().mustRevalidate(),
			"/api/categories", "/api/wishes/categories");

		registry.addInterceptor(webCacheContentInterceptor);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authPrincipalArgumentResolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}

	@Bean
	public FilterRegistrationBean<JwtAuthorizationFilter> jwtAuthorizationFilter() {
		FilterRegistrationBean<JwtAuthorizationFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
		filterFilterRegistrationBean.setFilter(
			new JwtAuthorizationFilter(jwtProvider, authenticationContext, objectMapper, redisService));
		filterFilterRegistrationBean.addUrlPatterns("/api/*");
		return filterFilterRegistrationBean;
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new WishRequestConverter());
		registry.addConverter(new SalesRequestConverter());
	}

	@Bean
	public FilterRegistrationBean<LogoutFilter> logoutFiler() {
		FilterRegistrationBean<LogoutFilter> logoutFilerBean = new FilterRegistrationBean<>();
		logoutFilerBean.setFilter(new LogoutFilter(redisService, objectMapper));
		logoutFilerBean.addUrlPatterns("/api/auth/logout");
		return logoutFilerBean;
	}
}
