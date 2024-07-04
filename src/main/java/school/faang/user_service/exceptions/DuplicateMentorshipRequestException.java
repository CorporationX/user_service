package school.faang.user_service.exceptions;

public class DuplicateMentorshipRequestException extends RuntimeException{

    public DuplicateMentorshipRequestException(String message) {
        super(message);
    }
}
