package school.faang.user_service.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDto {
    private String namePattern;

    private String aboutPattern;

    @Email(message = "Invalid email format")
    private String emailPattern;

    private String contactPattern;

    private String countryPattern;

    private String cityPattern;

    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phonePattern;

    private String skillPattern;

    @Min(value = 0, message = "Experience must be at least 0")
    private int experienceMin;

    @Min(value = 0, message = "Experience must be at least 0")
    private int experienceMax;

    @Min(value = 1, message = "Page must be at least 1")
    private int page;

    @Min(value = 1, message = "Page size must be at least 1")
    private int pageSize;
}
