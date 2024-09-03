package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class RequestFilterDto {
    private Long id;
    private RequestStatus status;
}
