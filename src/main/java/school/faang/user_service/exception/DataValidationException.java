package school.faang.user_service.exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException(MessageError messageError) {
        super(messageError.getMessage());
    }

    public DataValidationException(String messageError) {
        super(messageError);
    }

}
