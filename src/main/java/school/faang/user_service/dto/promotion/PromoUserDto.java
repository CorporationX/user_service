package school.faang.user_service.dto.promotion;

import lombok.Builder;

@Builder
public record PromoUserDto(
        Long id,
        String title,
        String description,
        String aboutMe,
        String username,
        String country,
        String city,
        String email
) {
}
