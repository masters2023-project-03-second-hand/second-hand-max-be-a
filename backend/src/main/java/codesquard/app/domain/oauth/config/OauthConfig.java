package codesquard.app.domain.oauth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import codesquard.app.domain.oauth.properties.OauthProperties;
import codesquard.app.domain.oauth.repository.InMemoryOauthClientRepository;
import lombok.RequiredArgsConstructor;

@EnableConfigurationProperties(OauthProperties.class)
@RequiredArgsConstructor
@Configuration
public class OauthConfig {

	private final OauthProperties oauthProperties;

	@Bean
	public InMemoryOauthClientRepository inMemoryOauthProviderRepository() {
		return new InMemoryOauthClientRepository(oauthProperties.createOauthClientMap());
	}

}
