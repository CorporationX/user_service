package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UserFilterDto {
    private String namePattern;
    private String aboutPattern;
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    private int experienceMin;
    private int experienceMax;
}