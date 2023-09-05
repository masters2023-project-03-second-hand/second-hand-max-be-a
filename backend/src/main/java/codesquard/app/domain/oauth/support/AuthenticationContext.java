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

	@Override
	public String toString() {
		return String.format("%s, %s(%s)", "인증 컨텍스트", this.getClass().getSimpleName(), principal);
	}
}
