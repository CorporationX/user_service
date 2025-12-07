package school.faang.user_service.dto.user;

import school.faang.user_service.entity.contact.PreferredContact;

public record UpdateUserDto(
        String username,
        String email,
        String password,
        Long countryId,
        String phone,
        String aboutMe,
        String city,
        Integer experience,
        PreferredContact contactPreference
) {
}
