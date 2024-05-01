package school.faang.user_service.dto.goal;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import school.faang.user_service.entity.RequestStatus;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationDto {
    Long id;

    Long inviterId;

    Long invitedUserId;

    Long goalId;

    RequestStatus status;
}
