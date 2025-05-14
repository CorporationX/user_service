package school.faang.user_service.dto.goal;

import lombok.*;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationFilterIDto {
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
