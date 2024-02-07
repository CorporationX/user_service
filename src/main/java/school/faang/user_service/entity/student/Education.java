
package school.faang.user_service.entity.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "faculty",
        "yearOfStudy",
        "major",
        "GPA"
})
@Generated("jsonschema2pojo")
@Data
public class Education {
    @JsonProperty("faculty")
    private String faculty;
    @JsonProperty("yearOfStudy")
    private Integer yearOfStudy;
    @JsonProperty("major")
    private String major;
    @JsonProperty("GPA")
    private Double gpa;
}
