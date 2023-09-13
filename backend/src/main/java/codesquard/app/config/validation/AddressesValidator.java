package codesquard.app.config.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import codesquard.app.annotation.Addresses;

public class AddressesValidator implements ConstraintValidator<Addresses, List<Long>> {

	private int min;
	private int max;

	@Override
	public void initialize(Addresses constraintAnnotation) {
		this.min = constraintAnnotation.min();
		this.max = constraintAnnotation.max();
	}

	@Override
	public boolean isValid(List<Long> values, ConstraintValidatorContext context) {

		if (values == null) {
			return false;
		}
		for (Long value : values) {
			if (value == null) {
				return false;
			}
			if (value <= 0L) {
				return false;
			}
		}

		if (values.size() < min || values.size() > max) {
			return false;
		}

		return true;
	}
}
