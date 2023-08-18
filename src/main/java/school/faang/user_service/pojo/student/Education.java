package school.faang.user_service.pojo.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    public String faculty;
    public Integer yearOfStudy;
    public String major;
    public Double gpa;
}
