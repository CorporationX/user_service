package school.faang.user_service.dto.user;

public record EducationDto(
        long id,
        Integer yearFrom,
        Integer yearTo,
        String institution,
        String educationLevel,
        String specialization
) {}
