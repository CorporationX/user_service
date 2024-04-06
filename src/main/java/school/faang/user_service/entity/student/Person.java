package school.faang.user_service.entity.student;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Person {
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentID;
    @JsonUnwrapped
    private Education education;
    @JsonUnwrapped
    private ContactInfo contactInfo;
    private String status;
    private String admissionDate;
    private String graduationDate;
    @JsonUnwrapped
    private List<PreviousEducation> previousEducation = new ArrayList<>();
    private Boolean scholarship;
    private String employer;
}
