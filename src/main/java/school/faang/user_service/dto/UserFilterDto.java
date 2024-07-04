package school.faang.user_service.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDto {
    private String namePattern;
    private String aboutPattern;
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    @Positive(message = "Value should be positive")
    private Integer experienceMin;
    @Positive(message = "Value should be positive")
    private Integer experienceMax;
    @Positive(message = "Value should be positive")
    @Builder.Default
    private int page = 1; //default value
    @Positive(message = "Value should be positive")
    @Builder.Default
    private int pageSize = 10; //default value
}
