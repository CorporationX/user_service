package school.faang.user_service.exception;

public class SameMentorAndMenteeException extends RuntimeException{
    public SameMentorAndMenteeException(String message) {
        super(message);
    }
}
