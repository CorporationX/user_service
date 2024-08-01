package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {

    public String firstName;

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

    @JsonProperty("previousEducation")
    private List<PreviousEducation> previousEducation = new ArrayList<>();

    private Boolean scholarship;

    private String employer;

    //@JsonProperty("previousEducation")
    @JsonUnwrapped
    public void addPreviousEducation(PreviousEducation previousEducation) {
        this.previousEducation.add(previousEducation);
    }

}
