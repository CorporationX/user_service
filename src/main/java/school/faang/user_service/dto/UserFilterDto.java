package school.faang.user_service.dto;

public record UserFilterDto(String namePattern, String phonePattern, int experienceMin, int experienceMax) {
}
