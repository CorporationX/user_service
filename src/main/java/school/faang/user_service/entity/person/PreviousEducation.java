
package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.annotation.processing.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class PreviousEducation {

    @JsonProperty("degree")
    private String degree;

    @JsonProperty("institution")
    private String institution;

    @JsonProperty("completionYear")
    private Integer completionYear;
}
