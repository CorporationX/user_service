package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserDto(
        @Nullable
        String username,

        @Nullable
        String email,

        @Nullable
        @NotBlank
        @Schema(description = "User’s phone number")
        String phone,

        @Nullable
        @Size(max = 500)
        @Schema(description = "Brief biography or personal description")
        String aboutMe,

        @Nullable
        @NotNull
        @Schema(description = "Identifier of the user’s country")
        Long countryId,

        @Nullable
        @Schema(description = "Name of the user’s city")
        String city
) {
}
