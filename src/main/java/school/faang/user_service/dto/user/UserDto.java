package school.faang.user_service.dto.user;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        boolean active,
        Integer experience
) {
}
