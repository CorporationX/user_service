package school.faang.user_service.validator.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

public interface MentorshipValidator {
    void validate(MentorshipRequestDto requestDto);
}
