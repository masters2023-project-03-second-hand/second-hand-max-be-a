package codesquard.app.config.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import codesquard.app.annotation.MessageIndex;

public class MessageIndexValidator implements ConstraintValidator<MessageIndex, Integer> {
	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return value >= 0;
	}
}
