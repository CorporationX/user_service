package school.faang.user_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import school.faang.user_service.validation.impl.NotEmptyFilterValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static school.faang.user_service.util.LogsConstants.EMPTY_FILTER;

@Constraint(validatedBy = NotEmptyFilterValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyFilter {
    String message() default EMPTY_FILTER;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
