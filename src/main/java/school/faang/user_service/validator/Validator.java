package school.faang.user_service.validator;


import school.faang.user_service.validator.validatorResult.ValidationResult;

import java.util.List;
import java.util.function.BiFunction;

@FunctionalInterface
public interface Validator<T,U,Z> {
    ValidationResult validate(T t, List<BiFunction<U,T,Z>> predicates);
}
