package school.faang.user_service.dto;

import java.util.Locale;

public record UserDto(
        Long id,
        String username,
        String email,
        Locale locale
) {
}
