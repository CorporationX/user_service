package school.faang.user_service.exception;

public class RequestMentorshipException extends RuntimeException{
    public RequestMentorshipException(String message) {
        super(message);
    }

    public RequestMentorshipException() {
    }
}
