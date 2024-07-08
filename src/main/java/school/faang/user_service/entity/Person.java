
package school.faang.user_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private ContactInfo contactInfo;

    @JsonUnwrapped
    private Education education;

    private String status;

    private String admissionDate;

    private String graduationDate;

    private List<PreviousEducation> previousEducation = new ArrayList<>();

    private Boolean scholarship;

    private String employer;

    @JsonProperty("previousEducation")
    @JsonUnwrapped
    public void addPreviousEducation(PreviousEducation prevEdu) {
        this.previousEducation.add(prevEdu);
    }
}
