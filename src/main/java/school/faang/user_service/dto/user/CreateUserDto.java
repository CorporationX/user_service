package school.faang.user_service.dto.user;

public record CreateUserDto(
        String username,
        String email,
        String password,
        Long countryId
) {
}
