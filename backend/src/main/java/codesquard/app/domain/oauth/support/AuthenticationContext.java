package codesquard.app.domain.oauth.support;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Getter;

@Getter
@Component
@RequestScope
public class AuthenticationContext {

	private Principal principal;

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}
}
