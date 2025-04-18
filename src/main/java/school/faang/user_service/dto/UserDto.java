package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

public record UserDto(
        Long id,
        @NotBlank
        @NotNull
        String username,
        @Email(message = "It's email")
        String email,
        @Nullable
        String fileId){
}
