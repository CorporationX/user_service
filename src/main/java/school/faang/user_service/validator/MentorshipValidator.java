package school.faang.user_service.validator;

import school.faang.user_service.exception.DataValidationException;

public class MentorshipValidator {
    public void validationForIdsNotEqual(Long mentorId, Long menteeId) {
        if (mentorId.equals(menteeId)) {
            throw new DataValidationException("Ids must not be equal");
        }
    }

    public void validationForNullOrLessThenOneUserId(Long userId) {
        if (userId == null || userId <= 0L) {
            throw new DataValidationException("id must be bigger than 0 and must not be null");
        }
    }
}
