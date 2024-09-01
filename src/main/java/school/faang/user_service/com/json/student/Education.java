
package school.faang.user_service.com.json.student;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "faculty",
    "yearOfStudy",
    "major",
    "GPA"
})
@Generated("jsonschema2pojo")
public class Education {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("faculty")
    private String faculty;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("yearOfStudy")
    private Integer yearOfStudy;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("major")
    private String major;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("GPA")
    private Double gpa;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("faculty")
    public String getFaculty() {
        return faculty;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("faculty")
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("yearOfStudy")
    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("yearOfStudy")
    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("major")
    public String getMajor() {
        return major;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("major")
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("GPA")
    public Double getGpa() {
        return gpa;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("GPA")
    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Education.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("faculty");
        sb.append('=');
        sb.append(((this.faculty == null)?"<null>":this.faculty));
        sb.append(',');
        sb.append("yearOfStudy");
        sb.append('=');
        sb.append(((this.yearOfStudy == null)?"<null>":this.yearOfStudy));
        sb.append(',');
        sb.append("major");
        sb.append('=');
        sb.append(((this.major == null)?"<null>":this.major));
        sb.append(',');
        sb.append("gpa");
        sb.append('=');
        sb.append(((this.gpa == null)?"<null>":this.gpa));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.gpa == null)? 0 :this.gpa.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.major == null)? 0 :this.major.hashCode()));
        result = ((result* 31)+((this.yearOfStudy == null)? 0 :this.yearOfStudy.hashCode()));
        result = ((result* 31)+((this.faculty == null)? 0 :this.faculty.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Education) == false) {
            return false;
        }
        Education rhs = ((Education) other);
        return ((((((this.gpa == rhs.gpa)||((this.gpa!= null)&&this.gpa.equals(rhs.gpa)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.major == rhs.major)||((this.major!= null)&&this.major.equals(rhs.major))))&&((this.yearOfStudy == rhs.yearOfStudy)||((this.yearOfStudy!= null)&&this.yearOfStudy.equals(rhs.yearOfStudy))))&&((this.faculty == rhs.faculty)||((this.faculty!= null)&&this.faculty.equals(rhs.faculty))));
    }

}
