package school.faang.user_service.validator;

import school.faang.user_service.dto.goal.GoalInvitationDto;

public interface InvintationDtoValidator {
    void validate(GoalInvitationDto goalInvitationDto);
}
