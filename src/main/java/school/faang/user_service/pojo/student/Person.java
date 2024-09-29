
package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @NotNull
    @NotBlank
    @JsonProperty("firstName")
    private String firstName;

    @NotNull
    @NotBlank
    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("yearOfBirth")
    private int yearOfBirth;

    @JsonProperty("group")
    private String group;

    @JsonProperty("studentID")
    private String studentID;

    @JsonUnwrapped
    private ContactInfo contactInfo;

    @JsonUnwrapped
    private Education education;

    @JsonProperty("status")
    private String status;

    @JsonProperty("admissionDate")
    private String admissionDate;

    @JsonProperty("graduationDate")
    private String graduationDate;

    @JsonUnwrapped
    private PreviousEducation previousEducation;

    @JsonProperty("scholarship")
    private Boolean scholarship;

    @NotNull
    @NotBlank
    private String employer;
}
