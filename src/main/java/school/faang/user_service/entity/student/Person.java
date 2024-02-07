package school.faang.user_service.entity.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "firstName",
        "lastName",
        "yearOfBirth",
        "group",
        "studentID",
        "contactInfo",
        "education",
        "status",
        "admissionDate",
        "graduationDate",
        "previousEducation",
        "scholarship",
        "employer"
})
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
    @JsonProperty("education")
    @JsonUnwrapped
    private Education education;
    @JsonProperty("contactInfo")
    @JsonUnwrapped
    private ContactInfo contactInfo;
    @JsonProperty("status")
    private String status;
    @JsonProperty("admissionDate")
    private String admissionDate;
    @JsonProperty("graduationDate")
    private String graduationDate;
    @JsonProperty("previousEducation")
    @JsonUnwrapped
    private List<PreviousEducation> previousEducation = new ArrayList<>();
    @JsonProperty("scholarship")
    private Boolean scholarship;
    @JsonProperty("employer")
    private String employer;
}
