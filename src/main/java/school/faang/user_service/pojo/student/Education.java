
package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Education {

    @NotNull
    @NotBlank
    private String faculty;

    @NotNull
    private int yearOfStudy;

    @NotNull
    @NotBlank
    private String major;

    @JsonProperty("GPA")
    private Double gpa;

    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}
