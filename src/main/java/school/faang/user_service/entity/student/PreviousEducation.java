
package school.faang.user_service.entity.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "degree",
        "institution",
        "completionYear"
})
@Generated("jsonschema2pojo")
@Data
public class PreviousEducation {
    @JsonProperty("degree")
    private String degree;
    @JsonProperty("institution")
    private String institution;
    @JsonProperty("completionYear")
    private Integer completionYear;
}
