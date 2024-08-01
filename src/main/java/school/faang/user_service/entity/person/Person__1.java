//package school.faang.user_service.entity.person;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import javax.annotation.processing.Generated;
//import com.fasterxml.jackson.annotation.JsonAnyGetter;
//import com.fasterxml.jackson.annotation.JsonAnySetter;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import com.fasterxml.jackson.annotation.JsonUnwrapped;
//import lombok.Data;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({
//        "firstName",
//        "lastName",
//        "yearOfBirth",
//        "group",
//        "studentID",
//        "contactInfo",
//        "education",
//        "status",
//        "admissionDate",
//        "graduationDate",
//        "previousEducation",
//        "scholarship",
//        "employer"
//})
//@Generated("jsonschema2pojo")
//@Data
//public class Person__1 {
//
//    @JsonProperty("firstName")
//    public String firstName;
//
//    @JsonProperty("lastName")
//    public String lastName;
//
//    @JsonProperty("yearOfBirth")
//    public Integer yearOfBirth;
//
//    @JsonProperty("group")
//    public String group;
//
//    @JsonProperty("studentID")
//    public String studentID;
//
//    //@JsonProperty("contactInfo")
//    @JsonUnwrapped
//    public ContactInfo contactInfo;
//
//    //@JsonProperty("education")
//    @JsonUnwrapped
//    public Education education;
//
//    @JsonProperty("status")
//    public String status;
//
//    @JsonProperty("admissionDate")
//    public String admissionDate;
//
//    @JsonProperty("graduationDate")
//    public String graduationDate;
//
//    //@JsonProperty("previousEducation")
//    @JsonUnwrapped
//    public List<PreviousEducation> previousEducation;
//
//    @JsonProperty("scholarship")
//    public Boolean scholarship;
//
//    @JsonProperty("employer")
//    public String employer;
//
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
//}