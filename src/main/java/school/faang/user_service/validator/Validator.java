package school.faang.user_service.validator;

public interface Validator<T> {
    boolean validate(final T data);
}
