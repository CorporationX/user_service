package school.faang.user_service.validator;


import school.faang.user_service.validator.validatorResult.ValidationResult;

@FunctionalInterface
public interface Validator<T> {
    ValidationResult validate(T t);
}
