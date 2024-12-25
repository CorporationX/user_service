package school.faang.user_service.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.Builder;
import school.faang.user_service.model.UserProfilePic;

@Builder
public record UserSubResponseDto(
    Long id,
    @NotEmpty String username,
    @NotEmpty String email,
    UserProfilePic avatarFile
) {
}