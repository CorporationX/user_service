package school.faang.user_service.dto.filter;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalInvitationFilterIDto {

    private Long inviterId;
    private Long invitedId;

    private RequestStatus status;

    private String inviterNamePattern;
    private String invitedNamePattern;
}
