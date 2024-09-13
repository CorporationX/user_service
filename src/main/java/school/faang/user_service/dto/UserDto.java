package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record UserDto(long id, String username, String email) {
}
