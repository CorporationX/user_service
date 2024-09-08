package school.faang.user_service.service.mentorship_request.errorMessages;

public class MentorshipRequestErrorMessages {
    public static final String REQUEST_IS_ACCEPTED_BEFORE =
            "Mentorship request from user with id %d to user with %d was accepted before";
    public static final String REQUEST_NOT_FOUND = "Mentorship request with id %d not found";
    public static final String EMPTY_DESCRIPTION = "Description is empty";
    public static final String REQUEST_TO_HIMSELF = "The user cannot send a request to himself";
    public static final String ONCE_EVERY_THREE_MONTHS = "A request for mentoring can be made only once every 3 months";
    public static final String USER_NOT_FOUND = "User with id %s not found";
}
