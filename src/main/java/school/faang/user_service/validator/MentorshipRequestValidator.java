package school.faang.user_service.validator;

import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.validator.validatorResult.ValidationResult;


public interface MentorshipRequestValidator {
    ValidationResult validate(MentorshipRequestDto mentorshipRequestDto);
}
