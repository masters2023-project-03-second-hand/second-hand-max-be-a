package codesquard.app.config.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import codesquard.app.annotation.Addresses;

public class AddressesValidator implements ConstraintValidator<Addresses, List<String>> {
	@Override
	public boolean isValid(List<String> values, ConstraintValidatorContext context) {
		if (values == null) {
			return false;
		}
		for (String value : values) {
			if (value == null) {
				return false;
			}
			if (value.isBlank()) {
				return false;
			}
		}
		return true;
	}
}
