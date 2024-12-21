package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationFilterDto {
    private String inviterNamePattern;
    private String invitedNamePattern;
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
