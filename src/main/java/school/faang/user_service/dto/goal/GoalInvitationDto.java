package school.faang.user_service.dto.goal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import school.faang.user_service.entity.RequestStatus;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class GoalInvitationDto {
    Long id;

    Long inviterId;

    Long invitedUserId;

    Long goalId;

    RequestStatus status;
}
