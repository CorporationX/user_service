package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        String email
) {
}

