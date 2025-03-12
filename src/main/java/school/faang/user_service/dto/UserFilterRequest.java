package school.faang.user_service.dto;

public record UserFilterRequest(
        String namePattern,
        String phonePattern,
        Integer experienceMin,
        Integer experienceMax
) {
}
