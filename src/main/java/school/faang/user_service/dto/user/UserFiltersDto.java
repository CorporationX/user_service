package school.faang.user_service.dto.user;

import lombok.Data;

@Data
public class UserFiltersDto {
    private String namePattern;
    private String phonePattern;
    private int experienceMin;
    private int experienceMax;
}

