package school.faang.user_service.exception.mentorship;

import java.util.NoSuchElementException;

/**
 * @author Evgenii Malkov
 */
public class MentorshipNoSuchElementException extends NoSuchElementException {

    public MentorshipNoSuchElementException(String message) {
        super(message);
    }

}
