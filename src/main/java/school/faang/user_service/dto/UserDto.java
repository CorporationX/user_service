package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
public record UserDto (
    Long userId,
    String username
){}