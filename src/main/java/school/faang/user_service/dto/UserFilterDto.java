package school.faang.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Filter criteria for searching users.")
public class UserFilterDto {

    @Schema(description = "Field-level description here")
    @Size(max = 255, message = "Description can't be longer than 255")
    private String namePattern;

    private String aboutPattern;

    @Size(max = 255, message = "Email can't be longer than 255")
    private String emailPattern;

    @Size(max = 255, message = "Contact name can't be longer than 255")
    private String contactPattern;

    @Size(max = 255, message = "Country name can't be longer than 255")
    private String countryPattern;

    @Size(max = 255, message = "City name can't be longer than 255")
    private String cityPattern;

    @Size(max = 255, message = "Phone number name can't be longer than 255")
    private String phonePattern;

    @Size(max = 255, message = "Skill name can't be longer than 255")
    private String skillPattern;

    @Min(1)
    private int experienceMin;

    private int experienceMax;

    private int page;

    private int pageSize;
}
