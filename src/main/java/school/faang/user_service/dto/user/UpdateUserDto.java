package school.faang.user_service.dto.user;

public record UpdateUserDto(
        String username,
        String email,
        String phone,
        String aboutMe,
        Long countryId,
        String city
) {
}
