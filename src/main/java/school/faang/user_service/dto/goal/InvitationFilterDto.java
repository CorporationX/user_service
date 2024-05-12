package school.faang.user_service.dto.goal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import school.faang.user_service.entity.RequestStatus;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvitationFilterDto {
    private String inviterNamePattern;

    private String invitedNamePattern;

    private Long inviterId;

    private Long invitedId;

    private RequestStatus status;
}
