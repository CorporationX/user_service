package school.faang.user_service.Exeptions;

public class DataValidationException extends Exception{
    public DataValidationException(String message){
        super(message);
    }
}
