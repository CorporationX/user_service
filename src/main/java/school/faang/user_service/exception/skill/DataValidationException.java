package school.faang.user_service.exception.skill;

public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }
}