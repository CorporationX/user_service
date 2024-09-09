package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InvitationFilterDto {
    private String inviterNamePattern;
    private String invitedNamePattern;
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
