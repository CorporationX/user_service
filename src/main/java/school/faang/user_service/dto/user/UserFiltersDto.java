package school.faang.user_service.dto.user;

public record UserFiltersDto(
        String namePattern,
        String phonePattern,
        int experienceMin,
        int experienceMax
) {
}