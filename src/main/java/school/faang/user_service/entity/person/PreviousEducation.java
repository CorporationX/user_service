package school.faang.user_service.entity.person;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonPropertyOrder({
//        "degree",
//        "institution",
//        "completionYear"
//})
//@Generated("jsonschema2pojo")
@Data
public class PreviousEducation {

    //@JsonProperty("degree")
    private String degree;

    //@JsonProperty("institution")
    private String institution;

    //@JsonProperty("completionYear")
    private Integer completionYear;

//    public String getDegree() {
//        return degree;
//    }
//
//    public void setDegree(String degree) {
//        this.degree = degree;
//    }
//
//    public String getInstitution() {
//        return institution;
//    }
//
//    public void setInstitution(String institution) {
//        this.institution = institution;
//    }
//
//    public Integer getCompletionYear() {
//        return completionYear;
//    }
//
//    public void setCompletionYear(Integer completionYear) {
//        this.completionYear = completionYear;
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