
package school.faang.user_service.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PreviousEducation {
    private String degree;
    private String institution;
    private int completionYear;
}
