package school.faang.user_service.exception;

public class RequestException extends RuntimeException{

    public RequestException(ErrorMessage message){
        super(message.getMessage());
    }
}
