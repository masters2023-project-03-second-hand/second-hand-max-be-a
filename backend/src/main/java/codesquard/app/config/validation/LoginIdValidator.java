package codesquard.app.config.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import codesquard.app.annotation.LoginId;

public class LoginIdValidator implements ConstraintValidator<LoginId, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		if (value.isBlank()) {
			return false;
		}

		return value.matches("^[a-zA-Z0-9]{2,12}$");
	}
}
