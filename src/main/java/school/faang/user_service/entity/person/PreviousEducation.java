
package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PreviousEducation {

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("institution")
    private String institution;

    @JsonProperty("completionYear")
    private Integer completionYear;
}
