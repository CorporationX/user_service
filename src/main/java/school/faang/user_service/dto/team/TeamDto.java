package school.faang.user_service.dto.team;

import school.faang.user_service.dto.user.UserDto;

import java.time.LocalDateTime;

public record TeamDto(
        Long id,
        String name,
        String description,
        UserDto manager,
        String avatarUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
