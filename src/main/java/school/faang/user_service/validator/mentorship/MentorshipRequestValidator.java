package school.faang.user_service.validator.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

public interface MentorshipRequestValidator {
    void validateMentorshipRequest(MentorshipRequestDto dto);
    MentorshipRequest validateMentorshipRequestExistence(long id);

    void validateMentor(MentorshipRequest entity);
}
