package school.faang.user_service.entity.person;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@JsonAutoDetect()
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonPropertyOrder({
//        "faculty",
//        "yearOfStudy",
//        "major",
//        "GPA"
//})
//@Generated("jsonschema2pojo")
@Data
public class Education {

    //@JsonProperty("faculty")
    private String faculty;

    //@JsonProperty("yearOfStudy")
    private Integer yearOfStudy;

    //@JsonProperty("major")
    private String major;

    //@JsonProperty("GPA")
    private Double gpa;

//    public String getFaculty() {
//        return faculty;
//    }
//
//    public void setFaculty(String faculty) {
//        this.faculty = faculty;
//    }
//
//    public Integer getYearOfStudy() {
//        return yearOfStudy;
//    }
//
//    public void setYearOfStudy(Integer yearOfStudy) {
//        this.yearOfStudy = yearOfStudy;
//    }
//
//    public String getMajor() {
//        return major;
//    }
//
//    public void setMajor(String major) {
//        this.major = major;
//    }
//
//    public Double getGpa() {
//        return gpa;
//    }
//
//    public void setGpa(Double gpa) {
//        this.gpa = gpa;
//    }
//    @JsonIgnore
//    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
//
//    @JsonAnyGetter
//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    @JsonAnySetter
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }
}
