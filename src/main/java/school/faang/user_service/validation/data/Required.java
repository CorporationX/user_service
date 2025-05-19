package school.faang.user_service.validation.data;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {
        DataRequiredStringValidator.class,
        DataRequiredLongValidator.class,
        DataRequiredLocalDateTimeValidator.class,
})

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
    String message() default "This field must be filled in";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
