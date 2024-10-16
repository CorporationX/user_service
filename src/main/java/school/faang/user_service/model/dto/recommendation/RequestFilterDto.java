package school.faang.user_service.model.dto.recommendation;

import lombok.Builder;
import school.faang.user_service.model.entity.RequestStatus;

@Builder
public record RequestFilterDto(Long id, RequestStatus status) {

}
