
package school.faang.user_service.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Education {
    private String faculty;
    private int yearOfStudy;
    private String major;
    private double GPA;
}
