package school.faang.user_service.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserFilterDto {
    private String namePattern;
    private String aboutPattern;
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    @Positive
    private Integer experienceMin;
    @Positive
    private Integer experienceMax;
    @Positive
    private int page = 1; //default value
    @Positive
    private int pageSize = 10; //default value
}
