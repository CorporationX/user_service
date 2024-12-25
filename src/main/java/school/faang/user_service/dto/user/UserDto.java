package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        @NotBlank String username,
        @NotBlank @Email String email
) {
}
