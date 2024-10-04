package school.faang.user_service.dto.user;

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
    private EducationDto education;
    private String employer;
    private int yearOfBirth;
    private String group;
    private String studentID;
}