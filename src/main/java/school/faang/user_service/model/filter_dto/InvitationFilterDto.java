package school.faang.user_service.model.filter_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.enums.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationFilterDto {
    private String inviterNamePattern;
    private String invitedNamePattern;
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
