package codesquard.app.domain.oauth.repository;

import codesquard.app.domain.oauth.client.OauthClient;

public interface OauthClientRepository {
	OauthClient findOneBy(String providerName);
}
