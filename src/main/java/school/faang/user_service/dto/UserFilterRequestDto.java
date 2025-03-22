package school.faang.user_service.dto;

public record UserFilterRequestDto(
        String namePattern,
        String phonePattern,
        Integer experienceMin,
        Integer experienceMax
) {
}
