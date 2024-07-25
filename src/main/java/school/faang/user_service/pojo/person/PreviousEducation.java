package school.faang.user_service.pojo.person;

import lombok.Data;

@Data
public class PreviousEducation {
    private String degree;
    private String institution;
    private int completionYear;
}
