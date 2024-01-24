package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Component
@RequiredArgsConstructor
public class MentorshipValidator {
    private final MentorshipRepository mentorshipRepository;
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

    public void validationMentorship(long receiverId, long requesterId) {
        if (mentorshipRepository.existsByMentorIdAndMenteeId(receiverId, requesterId)) {
            throw new DataValidationException("Пользователь уже является ментором отправителя");
        }
    }
}
