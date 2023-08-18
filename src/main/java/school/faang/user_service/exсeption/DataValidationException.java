package school.faang.user_service.exсeption;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message){
        super(message);
    }
}
