package school.faang.user_service.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import school.faang.user_service.validation.ValidTariffPlanPaymentValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTariffPlanPaymentValidator.class)
public @interface ValidTariffPlanPayment {
    String message() default "Invalid amount for selected tariff plan";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

