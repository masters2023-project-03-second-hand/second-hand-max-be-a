package codesquard.app.api.errors.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

	HttpStatus getHttpStatus();

	String getMessage();
}
