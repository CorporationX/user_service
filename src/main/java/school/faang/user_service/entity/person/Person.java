
package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Person {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("yearOfBirth")
    private Integer yearOfBirth;

    @JsonProperty("group")
    private String group;

    @JsonProperty("studentID")
    private String studentID;

    @JsonUnwrapped
    @JsonProperty("contactInfo")
    private ContactInfo contactInfo;

    @JsonUnwrapped
    @JsonProperty("education")
    private Education education;

    @JsonProperty("status")
    private String status;

    @JsonProperty("admissionDate")
    private String admissionDate;

    @JsonProperty("graduationDate")
    private String graduationDate;

    @JsonProperty("previousEducation")
    private List<PreviousEducation> previousEducation = new ArrayList<PreviousEducation>();

    @JsonProperty("scholarship")
    private Boolean scholarship;

    @JsonProperty("employer")
    private String employer;

    @JsonUnwrapped
    public void addPreviousEducation(PreviousEducation previousEducation) {
        this.previousEducation.add(previousEducation);
    }
}
