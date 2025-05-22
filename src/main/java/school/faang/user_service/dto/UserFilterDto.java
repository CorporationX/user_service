package school.faang.user_service.dto;

import lombok.Data;


@Data
public class UserFilterDto {
    private String namePattern;
    private String phonePattern;
    private int experienceMin;
    private int experienceMax;
}
