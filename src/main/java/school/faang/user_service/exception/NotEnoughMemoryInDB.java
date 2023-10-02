package school.faang.user_service.exception;

public class NotEnoughMemoryInDB extends RuntimeException{
    public NotEnoughMemoryInDB(String message) {
        super(message);
    }
}
