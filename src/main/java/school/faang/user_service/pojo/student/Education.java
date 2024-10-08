
package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Education {

    @NotNull
    @NotBlank
    private String faculty;

    private int yearOfStudy;

    @NotNull
    @NotBlank
    private String major;

    @JsonProperty("GPA")
    private Double gpa;
}
