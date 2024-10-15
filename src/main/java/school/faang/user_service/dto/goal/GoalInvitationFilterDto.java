package school.faang.user_service.dto.goal;

import lombok.Getter;
import school.faang.user_service.entity.RequestStatus;


@Getter
public class GoalInvitationFilterDto {
    private String inviterNamePattern;

    private String invitedNamePattern;

    private Long inviterId;

    private Long invitedId;

    private RequestStatus status;
}
