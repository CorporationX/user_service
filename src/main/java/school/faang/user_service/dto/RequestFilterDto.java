package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record RequestFilterDto(Long id, RequestStatus status) {

}
