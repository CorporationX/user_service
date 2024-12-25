package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserProfileUpdateDto(
        @NotBlank
        @Length(max = 64)
        String username,

        @Email
        @Length(max = 64)
        @NotBlank
        String email,

        @NotBlank
        @Length(max = 32)
        String phone,

        @NotBlank
        @Length(max = 4096)
        String aboutMe,

        @Positive
        @NotNull
        Long countryId,

        @NotBlank
        @Length(max = 64)
        String city,

        @Positive
        @NotNull
        Integer experience
) {
}
