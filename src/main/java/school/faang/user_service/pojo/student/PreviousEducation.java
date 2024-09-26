
package school.faang.user_service.pojo.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class PreviousEducation {

    @NotNull
    @NotBlank
    private String degree;

    @NotNull
    @NotBlank
    private String institution;

    private int completionYear;

    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
}
