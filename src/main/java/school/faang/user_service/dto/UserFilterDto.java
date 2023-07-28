package school.faang.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Filter criteria for searching users.")
public class UserFilterDto {
    @Schema(description = "Field-level description here")
    private String namePattern;
    private String aboutPattern;
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    @Min(1)
    private int experienceMin;
    private int experienceMax;
    private int page;
    private int pageSize;
}
