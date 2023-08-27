package codesquard.app.domain.oauth;

import java.util.Map;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import codesquard.app.domain.oauth.repository.InMemoryProviderRepository;
import codesquard.app.domain.oauth.repository.ProviderRepository;

@Configuration
@EnableConfigurationProperties(OauthProperties.class)
public class OauthConfig {

	@Bean
	public ProviderRepository providerRepository(OauthProperties oauthProperties) {
		Map<String, OauthProvider> oauthProviderMap = oauthProperties.createOauthProviderMap();
		return new InMemoryProviderRepository(oauthProviderMap);
	}
}
