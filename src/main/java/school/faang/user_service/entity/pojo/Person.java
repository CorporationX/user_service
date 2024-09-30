
package school.faang.user_service.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Person {
    private String firstName;
    private String lastName;
    private int yearOfBirth;
    private String group;
    private String studentID;
    private ContactInfo contactInfo;
    private Education education;
    private String status;
    private String admissionDate;
    private String graduationDate;
    private List<PreviousEducation> previousEducation;
    private boolean scholarship;
    private String employer;
}
