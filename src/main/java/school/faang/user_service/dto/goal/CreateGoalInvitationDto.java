package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateGoalInvitationDto {
    private final Long goalId;
    private final Long invitedUserId;
}
