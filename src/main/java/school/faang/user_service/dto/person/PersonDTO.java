package school.faang.user_service.dto.person;

import lombok.Data;

@Data
public class PersonDTO {
    private String username;
    private String email;
    private String phone;
    private String city;
    private String country;
    private String aboutMe;
    private String password;
    private String state;
    private String faculty;
    private String yearOfStudy;
    private String major;
    private String employer;
}
