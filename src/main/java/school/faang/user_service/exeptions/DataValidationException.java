package school.faang.user_service.exeptions;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message){
        super(message);
    }
}
