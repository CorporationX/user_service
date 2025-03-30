package school.faang.user_service.service.rating.annotation;

import school.faang.user_service.enums.RatingType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RatingChanging {
    RatingType ratingType();

    boolean positiveAction() default true;

}