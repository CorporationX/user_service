package school.faang.user_service.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.Builder;

@Builder
public record UserDto(
    Long id,
    @NotEmpty String username,
    @NotEmpty String email
) {
}