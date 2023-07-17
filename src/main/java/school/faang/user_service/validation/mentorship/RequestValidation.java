package school.faang.user_service.validation.mentorship;

import school.faang.user_service.exception.mentorship.InvalidRequestMentorId;

public class RequestValidation {
    public static void checkValidityId(long id) {
        if (id <= 0) {
            throw new InvalidRequestMentorId("Invalid request: bad user id");
        }
    }
}
