package school.faang.user_service.exception.remote;

public class AmazonS3CustomException extends RuntimeException {
    public AmazonS3CustomException(String message) {
        super(message);
    }
}
