package school.faang.user_service.validator;

import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.validator.validatorResult.ValidationResult;


public interface MentorshipRequestValidator {
    public ValidationResult validate(MentorshipRequestDto mentorshipRequestDto);
}
