package school.faang.user_service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFilterDto {
    @Size(max = 255, message = "max pattern length 255 characters")
    private String namePattern;

    @Size(max = 255, message = "max pattern length 255 characters")
    private String aboutPattern;

    @Size(max = 255, message = "max pattern length 255 characters")
    private String emailPattern;

    @Size(max = 255, message = "max pattern length 255 characters")
    private String contactPattern;

    @Size(max = 255, message = "max pattern length 255 characters")
    private String countryPattern;

    @Size(max = 255, message = "max pattern length 255 characters")
    private String cityPattern;

    @Size(max = 255, message = "max pattern length 255 characters")
    private String phonePattern;

    @Size(max = 255, message = "max pattern length 255 characters")
    private String skillPattern;

    private Integer experienceMin;
    private Integer experienceMax;
}
