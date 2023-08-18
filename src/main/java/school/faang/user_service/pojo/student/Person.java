package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    public String firstName;
    public String lastName;
    public Integer yearOfBirth;
    public String group;
    public String studentID;
    @JsonUnwrapped
    public ContactInfo contactInfo;
    @JsonUnwrapped
    public Education education;
    public String status;
    public String admissionDate;
    public String graduationDate;
    @JsonUnwrapped
    private PreviousEducation previousEducation;
    public Boolean scholarship;
    public String employer;
}
