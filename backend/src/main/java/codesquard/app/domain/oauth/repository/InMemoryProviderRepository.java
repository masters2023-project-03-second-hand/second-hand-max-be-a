package codesquard.app.domain.oauth.repository;

import java.util.HashMap;
import java.util.Map;

import codesquard.app.api.errors.errorcode.ProviderErrorCode;
import codesquard.app.api.errors.exception.RestApiException;
import codesquard.app.domain.oauth.OauthProvider;

public class InMemoryProviderRepository implements ProviderRepository {

	private final Map<String, OauthProvider> oauthProviderMap;

	public InMemoryProviderRepository(Map<String, OauthProvider> oauthProviderMap) {
		this.oauthProviderMap = new HashMap<>(oauthProviderMap);
	}

	@Override
	public OauthProvider findByProviderName(String name) {
		OauthProvider oauthProvider = oauthProviderMap.get(name);
		if (oauthProvider == null) {
			throw new RestApiException(ProviderErrorCode.NOT_FOUND_PROVIDER);
		}
		return oauthProviderMap.get(name);
	}
}
