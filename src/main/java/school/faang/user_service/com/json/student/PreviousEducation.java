
package school.faang.user_service.com.json.student;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "degree",
    "institution",
    "completionYear"
})
@Generated("jsonschema2pojo")
public class PreviousEducation {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("degree")
    private String degree;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("institution")
    private String institution;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("completionYear")
    private Integer completionYear;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("degree")
    public String getDegree() {
        return degree;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("degree")
    public void setDegree(String degree) {
        this.degree = degree;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("institution")
    public String getInstitution() {
        return institution;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("institution")
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("completionYear")
    public Integer getCompletionYear() {
        return completionYear;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("completionYear")
    public void setCompletionYear(Integer completionYear) {
        this.completionYear = completionYear;
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
        sb.append(PreviousEducation.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("degree");
        sb.append('=');
        sb.append(((this.degree == null)?"<null>":this.degree));
        sb.append(',');
        sb.append("institution");
        sb.append('=');
        sb.append(((this.institution == null)?"<null>":this.institution));
        sb.append(',');
        sb.append("completionYear");
        sb.append('=');
        sb.append(((this.completionYear == null)?"<null>":this.completionYear));
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
        result = ((result* 31)+((this.degree == null)? 0 :this.degree.hashCode()));
        result = ((result* 31)+((this.institution == null)? 0 :this.institution.hashCode()));
        result = ((result* 31)+((this.completionYear == null)? 0 :this.completionYear.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PreviousEducation) == false) {
            return false;
        }
        PreviousEducation rhs = ((PreviousEducation) other);
        return (((((this.degree == rhs.degree)||((this.degree!= null)&&this.degree.equals(rhs.degree)))&&((this.institution == rhs.institution)||((this.institution!= null)&&this.institution.equals(rhs.institution))))&&((this.completionYear == rhs.completionYear)||((this.completionYear!= null)&&this.completionYear.equals(rhs.completionYear))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

}
