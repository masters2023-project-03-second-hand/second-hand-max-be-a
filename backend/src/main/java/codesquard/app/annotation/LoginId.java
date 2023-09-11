package codesquard.app.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import codesquard.app.config.validation.LoginIdValidator;

@Target(value = FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginIdValidator.class)
public @interface LoginId {

	String message() default "아이디는 띄어쓰기 없이 영문, 숫자로 구성되며 2~12글자로 구성되어야 합니다.";

	Class[] groups() default {};

	Class[] payload() default {};
}
