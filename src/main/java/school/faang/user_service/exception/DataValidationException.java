package school.faang.user_service.exception;

public class DataValidationException extends RuntimeException { //я не знаю как лучше назвать класс exception
    public DataValidationException(){
        super("The recommendation you submitted is missing text");
    }
}
