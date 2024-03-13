package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilterDto {
    private Long id;
    private RequestStatus status;
}
