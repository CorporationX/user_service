package school.faang.user_service.dto.goal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import school.faang.user_service.entity.RequestStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationDto {
    Long id;

    Long inviterId;

    Long invitedUserId;

    Long goalId;

    RequestStatus status;
}
