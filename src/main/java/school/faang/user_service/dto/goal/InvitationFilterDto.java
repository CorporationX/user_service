package school.faang.user_service.dto.goal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import school.faang.user_service.entity.RequestStatus;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvitationFilterDto {
    String inviterNamePattern;

    String invitedNamePattern;

    Long inviterId;

    Long invitedId;

    RequestStatus status;
}
