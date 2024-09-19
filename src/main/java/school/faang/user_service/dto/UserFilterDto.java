package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @Size(min = 2, max = 50, message = "Name pattern must be between 2 and 50 characters")
    private String namePattern;

    @Size(max = 250, message = "About pattern must not exceed 250 characters")
    private String aboutPattern;

    @Email
    private String emailPattern;

    @Pattern(regexp = "\\d{10}", message = "Contact pattern must be a valid 10-digit number")
    private String contactPattern;

    @Size(min = 2, max = 50, message = "Country pattern must be between 2 and 50 characters")
    private String countryPattern;

    @Size(min = 2, max = 50, message = "City pattern must be between 2 and 50 characters")
    private String cityPattern;

    @Pattern(regexp = "\\+?[0-9.()-]{10,25}", message = "Invalid phone number format")
    private String phonePattern;

    @Size(max = 100, message = "Skill pattern must not exceed 100 characters")
    private String skillPattern;

    @Min(value = 0, message = "Experience minimum must be at least 0")
    @Max(value = 50, message = "Experience minimum must not exceed 50 years")
    private int experienceMin;

    @Min(value = 0, message = "Experience maximum must be at least 0")
    @Max(value = 50, message = "Experience maximum must not exceed 50 years")
    private int experienceMax;

    @Min(value = 1, message = "Page must be at least 1")
    private int page;

    @Min(value = 1, message = "Page size must be at least 1")
    private int pageSize;
}
