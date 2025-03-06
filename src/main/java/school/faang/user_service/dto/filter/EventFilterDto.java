package school.faang.user_service.dto.filter;

import lombok.Builder;

@Builder
public record EventFilterDto(Long id,
                             String title) {

}
