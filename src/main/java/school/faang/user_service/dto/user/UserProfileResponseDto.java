package school.faang.user_service.dto.user;

import lombok.Builder;
import school.faang.user_service.dto.CountryResponseDto;

@Builder
public record UserProfileResponseDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        CountryResponseDto country,
        String city
) {
}
