package school.faang.user_service.controller.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.RequestStatus;

@AllArgsConstructor
@Getter
public class InvitationFilterDto {
    private String inviterNamePattern;
    private String invitedNamePattern;
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
