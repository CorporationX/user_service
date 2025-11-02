package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

@Getter
@RequiredArgsConstructor
public class GoalInvitationDto {
    private final Long id;
    private final Long goalId;
    private final UserDto inviter;
    private final UserDto invited;
    private final RequestStatus status;
}
