package codesquard.app.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import codesquard.app.config.validation.MessageIndexValidator;

@Target(value = {PARAMETER, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MessageIndexValidator.class)
public @interface MessageIndex {

	String message() default "messageIndex는 0 이상이어야 합니다.";

	Class[] groups() default {};

	Class[] payload() default {};
}
