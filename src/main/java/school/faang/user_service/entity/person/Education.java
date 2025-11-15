package school.faang.user_service.entity.person;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @NotNull
    private String faculty;

    @NotNull
    private Integer yearOfStudy;

    @NotNull
    private String major;

    @NotNull
    private Double gpa;
}