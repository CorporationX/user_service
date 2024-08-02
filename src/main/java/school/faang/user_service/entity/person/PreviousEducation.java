package school.faang.user_service.entity.person;

import lombok.Data;

@Data
public class PreviousEducation {

    private String degree;

    private String institution;

    private Integer completionYear;
}