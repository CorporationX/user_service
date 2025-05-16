package school.faang.user_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    public static final String PHONE_NUMBER_REGEX = "^\\+?\\d+$";
    @Pattern(
            regexp = PHONE_NUMBER_REGEX,
            message = "Number must contain only numbers and may start with +"
    )
    private String namePattern;
    @Max(value = 15, message = "The maximum length of phone number is 15")
    private String phonePattern;
    private Integer experienceMin;
    private Integer experienceMax;
}
