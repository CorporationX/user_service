
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
public class PreviousEducation {

    @NotNull
    @NotBlank
    @JsonProperty("degree")
    private String degree;

    @NotNull
    @NotBlank
    @JsonProperty("institution")
    private String institution;

    @JsonProperty("completionYear")
    private int completionYear;

    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}
