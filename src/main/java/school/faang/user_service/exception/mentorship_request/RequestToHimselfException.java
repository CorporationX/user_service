package school.faang.user_service.exception.mentorship_request;

import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.REQUEST_TO_HIMSELF;

public class RequestToHimselfException extends MentorshipRequestException {
    public RequestToHimselfException() {
        super(REQUEST_TO_HIMSELF);
    }
}
