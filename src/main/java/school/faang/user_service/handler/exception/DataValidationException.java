package school.faang.user_service.handler.exception;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message){
        super(message);
    }
}