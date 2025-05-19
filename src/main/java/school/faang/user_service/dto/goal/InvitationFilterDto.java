package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class InvitationFilterDto {
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
    private LocalDateTime createdBefore;
    private LocalDateTime createdAfter;
    private Integer offset;
    private Integer size;
    private SortOption sort;
}

