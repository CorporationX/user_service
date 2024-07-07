package school.faang.user_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Education {

    private String faculty;

    private Integer yearOfStudy;

    private String major;

    @JsonProperty("GPA")
    private Float gpa;
}
