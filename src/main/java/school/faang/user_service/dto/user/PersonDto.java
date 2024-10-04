package school.faang.user_service.dto.user;

import com.json.student.Education;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {
    private String firstName;
    private String lastName;
    private ContactInfoDto contactInfo;
    private Education education;
    private String employer;
}