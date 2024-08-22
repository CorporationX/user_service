
package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.annotation.processing.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

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
