
package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Education {

    @NotNull
    @NotBlank
    @JsonProperty("faculty")
    private String faculty;

    @JsonProperty("yearOfStudy")
    private int yearOfStudy;

    @NotNull
    @NotBlank
    @JsonProperty("major")
    private String major;

    @JsonProperty("GPA")
    private Double gpa;

    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}
