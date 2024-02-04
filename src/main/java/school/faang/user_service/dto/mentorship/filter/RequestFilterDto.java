package school.faang.user_service.dto.mentorship.filter;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilterDto {
    private String descriptionFilter;
    private Long requesterFilter;
    private Long receiverFilter;
    private RequestStatus statusFilter;
}
