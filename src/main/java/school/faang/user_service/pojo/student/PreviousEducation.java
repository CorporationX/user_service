package school.faang.user_service.pojo.student;

import lombok.Data;

@Data
public class PreviousEducation {
    public String degree;
    public String institution;
    public Integer completionYear;
}
