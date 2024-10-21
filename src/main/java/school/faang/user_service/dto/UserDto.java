package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(@NotNull(message = "Id cannot be empty.") Long id,
                      @NotBlank(message = "Name cannot be empty") String username,
                      @NotBlank(message = "Email is required")
                      @Email(message = "Email should be valid") String email) {
}
