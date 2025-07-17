package school.faang.user_service.validator;

public interface Validator<T, T2> {
    ValidationResult validate(T object, T2 object2);

    default boolean applicable(T object, T2 object2) {
        return true;
    }
}
