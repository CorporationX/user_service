package school.faang.user_service.dto.user;

import jakarta.annotation.Nullable;

public record UpdateUserDto(
        @Nullable
        String username,
        @Nullable
        String email,
        @Nullable
        String phone,
        @Nullable
        String aboutMe,
        @Nullable
        Long countryId,
        @Nullable
        String city
) {
}
