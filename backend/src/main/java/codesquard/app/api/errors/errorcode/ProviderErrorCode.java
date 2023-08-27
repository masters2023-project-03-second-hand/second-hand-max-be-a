package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ProviderErrorCode implements ErrorCode {

	NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "provider를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ProviderErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public String getName() {
		return name();
	}
}
