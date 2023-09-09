package codesquard.app.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import codesquard.app.config.validation.AddressesValidator;

@Target(value = FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AddressesValidator.class)
public @interface Addresses {

	String message() default "동네 주소는 최소 1개 최대 2개를 입력해주세요.";

	int min() default 1;

	int max() default 2;

	Class[] groups() default {};

	Class[] payload() default {};
}
