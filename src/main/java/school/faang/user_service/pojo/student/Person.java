
package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    private int yearOfBirth;

    private String group;

    private String studentID;

    @JsonUnwrapped
    private ContactInfo contactInfo;

    @JsonUnwrapped
    private Education education;

    private String status;

    private String admissionDate;

    private String graduationDate;

    @JsonUnwrapped
    private PreviousEducation previousEducation;

    private Boolean scholarship;

    @NotNull
    @NotBlank
    private String employer;
}
