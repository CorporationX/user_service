package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalInvitationFilterDto {
    private String inviterNamePattern;
    private String invitedNamePattern;
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
