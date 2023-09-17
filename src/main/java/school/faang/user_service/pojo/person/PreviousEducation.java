package school.faang.user_service.pojo.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousEducation {
    public String degree;
    public String institution;
    public Integer completionYear;
}
