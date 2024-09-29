
package school.faang.user_service.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Education {
    private String faculty;
    private Integer yearOfStudy;
    private String major;
    private Double gpa;
}
