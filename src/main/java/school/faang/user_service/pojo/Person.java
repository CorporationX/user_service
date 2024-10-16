
package school.faang.user_service.pojo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentId;
    @JsonUnwrapped
    private ContactInfo contactInfo;
    @JsonUnwrapped
    private Education education;
    private String status;
    private String admissionDate;
    private String graduationDate;
    @JsonUnwrapped
    private List<PreviousEducation> previousEducation = new ArrayList<>();
    private Boolean scholarship;
    private String employer;
}
