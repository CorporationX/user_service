package school.faang.user_service.service.recommendation.request.validator;

public interface Validator<T> {
    boolean validate(final T data);
}
