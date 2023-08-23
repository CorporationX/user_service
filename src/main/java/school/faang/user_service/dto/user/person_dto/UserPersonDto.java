package school.faang.user_service.dto.user.person_dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class UserPersonDto {
    @EqualsAndHashCode.Include
    private String username;
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentID;
    @EqualsAndHashCode.Include
    private ContactInfoDto contactInfo;
    private EducationDto education;
    private String status;
    private String admissionDate;
    private String graduationDate;
    private List<PreviousEducationDto> previousEducation = new ArrayList<>();
    private Boolean scholarship;
    private String employer;
}
