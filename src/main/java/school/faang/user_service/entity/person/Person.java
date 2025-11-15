package school.faang.user_service.entity.person;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private Integer yearOfBirth;

    @NotNull
    private String group;

    @NotNull
    private String studentId;

    @NotNull
    private ContactInfo contactInfo;

    @NotNull
    private List<Education> educations;

    private String status;

    private String admissionDate;

    private String graduationDate;

    @NotNull
    private List<PreviousEducation> previousEducation;

    private Boolean scholarship;

    private String employer;
}

