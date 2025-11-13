package school.faang.user_service.dto.user.education;

import jakarta.validation.constraints.*;
public record CreateEducationDto (

    @NotNull(message = "Year from cannot be null")
    @Min(value = 1900, message = "Year from must be after 1900")
    @Max(value = 2100, message = "Year from must be before 2100")
    Integer yearFrom,

    @NotNull(message = "Year to cannot be null")
    @Min(value = 1900, message = "Year to must be after 1900")
    @Max(value = 2100, message = "Year to must be before 2100")
    Integer yearTo,

    @NotBlank(message = "Institution cannot be blank")
    @Size(min = 1, max = 200, message = "Institution name must be between 1 and 200 characters")
    String institution,

    @NotBlank(message = "Education level cannot be blank")
    @Size(min = 1, max = 100, message = "Education level must be between 1 and 100 characters")
    String educationLevel,

    @NotBlank(message = "Specialization cannot be blank")
    @Size(min = 1, max = 200, message = "Specialization must be between 1 and 200 characters")
    String specialization)

{}
