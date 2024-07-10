package school.faang.user_service.exception.mentorship;

public class MentorshipIsAlreadyAgreedException extends RuntimeException {

    public MentorshipIsAlreadyAgreedException(String message) {
        super(message);
    }
}