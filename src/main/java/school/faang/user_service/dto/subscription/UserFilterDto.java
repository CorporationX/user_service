package school.faang.user_service.dto.subscription;

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
    private Integer experienceMin;
    private Integer experienceMax;
    private Integer page;
    private Integer pageSize;
}
