package codesquard.app.domain.oauth.repository;

import codesquard.app.domain.oauth.OauthProvider;

public interface ProviderRepository {
	OauthProvider findByProviderName(String name);
}
