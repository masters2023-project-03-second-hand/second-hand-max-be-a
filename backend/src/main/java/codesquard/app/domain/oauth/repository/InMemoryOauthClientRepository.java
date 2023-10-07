package codesquard.app.domain.oauth.repository;

import java.util.Map;

import codesquard.app.api.errors.errorcode.OauthErrorCode;
import codesquard.app.api.errors.exception.NotFoundResourceException;
import codesquard.app.domain.oauth.client.OauthClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InMemoryOauthClientRepository implements OauthClientRepository {

	private final Map<String, OauthClient> oauthClientMap;

	@Override
	public OauthClient findOneBy(String providerName) {
		OauthClient oauthClient = oauthClientMap.get(providerName);
		if (oauthClient == null) {
			throw new NotFoundResourceException(OauthErrorCode.NOT_FOUND_PROVIDER);
		}
		return oauthClient;
	}
}
