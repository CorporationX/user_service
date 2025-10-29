package school.faang.user_service.dto.user;

public record UserFiltersDto(
        String namePattern,
        String phoneNumber,
        int experienceMin,
        int experienceMax
) {
}
