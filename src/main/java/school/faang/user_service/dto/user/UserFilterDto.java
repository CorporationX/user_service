package school.faang.user_service.dto.user;

import lombok.Data;
import school.faang.user_service.entity.premium.Premium;

@Data
public class UserFilterDto {
    private String namePattern;
    private String phonePattern;
    private String emailPattern;
    private String aboutPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String skillPattern;
    private int experienceMin;
    private int experienceMax;
    private int page;
    private int pageSize;
    private Long id;
    private Premium premium;
}
