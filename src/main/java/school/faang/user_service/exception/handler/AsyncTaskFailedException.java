package school.faang.user_service.exception.handler;

public class AsyncTaskFailedException extends RuntimeException {

    public AsyncTaskFailedException(String message) {
        super(message);
    }
}
