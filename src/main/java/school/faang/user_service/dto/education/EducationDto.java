package school.faang.user_service.dto.education;

public record EducationDto(
        long id,
        Integer yearFrom,
        Integer yearTo,
        String institution,
        String educationLevel,
        String specialization
) {
}