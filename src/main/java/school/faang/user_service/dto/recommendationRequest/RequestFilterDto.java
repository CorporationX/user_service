package school.faang.user_service.dto.recommendationRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private RequestStatus statusFilter;
}
