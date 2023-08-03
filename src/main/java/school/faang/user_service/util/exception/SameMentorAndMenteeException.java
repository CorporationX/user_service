package school.faang.user_service.util.exception;

public class SameMentorAndMenteeException extends RuntimeException{
    public SameMentorAndMenteeException(String message) {
        super(message);
    }
}
