package school.faang.user_service.valitator;

public interface Validator<T> {

    ValidationResult validate(T object);
}
