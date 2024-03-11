
package school.faang.user_service.entity.student;

import lombok.Data;

@Data
public class Education {
    private String faculty;
    private Integer yearOfStudy;
    private String major;
    private Double gpa;
}
