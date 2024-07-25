package school.faang.user_service.pojo.person;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Status {
    private Date admissionDate;
    private Date graduationDate;
    private List<PreviousEducation> previousEducations;
}
