package school.faang.user_service.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import school.faang.user_service.validation.ValidCurrencyValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCurrencyValidator.class)
public @interface ValidCurrency {
    String message() default "Invalid currency for payment: can be \"USD, EUR\"";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
