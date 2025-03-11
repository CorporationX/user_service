package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.NonNull;
import school.faang.user_service.entity.contact.PreferredContact;

@Builder
@Schema(description = "DTO пользователя")
public record UserDto(
        @NonNull
        @Schema(description = "ID пользователя", example = "1")
        Long id,
        @NotEmpty(message = "Имя не может быть пустым")
        @Max(value = 64, message = "Имя не должно быть длиннее 64 символов")
        @Schema(description = "Имя пользователя", example = "JohnDoe")
        String username,
        @Email
        @Schema(description = "Email пользователя", example = "johndoe@example.com")
        String email,
        @Schema(description = "Предпочтительный способ связи", example = "EMAIL")
        PreferredContact preference
) {
}