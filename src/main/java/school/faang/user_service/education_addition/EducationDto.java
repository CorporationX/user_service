package school.faang.user_service.education_addition;

import lombok.Data;
import school.faang.user_service.entity.User;

@Data
public class EducationDto extends User {
    private long id;
    private Integer yearFrom;
    private Integer yearTo;
    private String institution;
    private String educationLevel;
    private String specialization;

}