//package school.faang.user_service.entity.person;
//
//import java.util.LinkedHashMap;
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
//        "person"
//})
//@Generated("jsonschema2pojo")
//@Data
//public class Person {
//
//    //@JsonProperty("person")
//    @JsonUnwrapped
//    public Person__1 person;
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

package school.faang.user_service.entity.person;

import java.util.List;

import lombok.Data;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
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
@Data
public class Person1 {

    //@JsonProperty("firstName")
    private String firstName;

    //@JsonProperty("lastName")
    public String lastName;

    //@JsonProperty("yearOfBirth")
    public Integer yearOfBirth;

    //@JsonProperty("group")
    public String group;

    //@JsonProperty("studentID")
    public String studentID;

    //@JsonProperty("contactInfo")
    //@JsonUnwrapped
    public ContactInfo contactInfo;

    //@JsonProperty("education")
    //@JsonUnwrapped
    public Education education;

    //@JsonProperty("status")
    public String status;

    //@JsonProperty("admissionDate")
    public String admissionDate;

    //@JsonProperty("graduationDate")
    public String graduationDate;

    //@JsonProperty("previousEducation")
    //@JsonUnwrapped
    public List<PreviousEducation> previousEducation;

    //@JsonProperty("scholarship")
    public Boolean scholarship;

    //@JsonProperty("employer")
    public String employer;

//    //@JsonProperty("firstName")
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public Integer getYearOfBirth() {
//        return yearOfBirth;
//    }
//
//    public void setYearOfBirth(Integer yearOfBirth) {
//        this.yearOfBirth = yearOfBirth;
//    }
//
//    public String getGroup() {
//        return group;
//    }
//
//    public void setGroup(String group) {
//        this.group = group;
//    }
//
//    public String getStudentID() {
//        return studentID;
//    }
//
//    public void setStudentID(String studentID) {
//        this.studentID = studentID;
//    }
//
//    public ContactInfo getContactInfo() {
//        return contactInfo;
//    }
//
//    public void setContactInfo(ContactInfo contactInfo) {
//        this.contactInfo = contactInfo;
//    }
//
//    public Education getEducation() {
//        return education;
//    }
//
//    public void setEducation(Education education) {
//        this.education = education;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getAdmissionDate() {
//        return admissionDate;
//    }
//
//    public void setAdmissionDate(String admissionDate) {
//        this.admissionDate = admissionDate;
//    }
//
//    public String getGraduationDate() {
//        return graduationDate;
//    }
//
//    public void setGraduationDate(String graduationDate) {
//        this.graduationDate = graduationDate;
//    }
//
//    public List<PreviousEducation> getPreviousEducation() {
//        return previousEducation;
//    }
//
//    public void setPreviousEducation(List<PreviousEducation> previousEducation) {
//        this.previousEducation = previousEducation;
//    }
//
//    public Boolean getScholarship() {
//        return scholarship;
//    }
//
//    public void setScholarship(Boolean scholarship) {
//        this.scholarship = scholarship;
//    }
//
//    public String getEmployer() {
//        return employer;
//    }
//
//    public void setEmployer(String employer) {
//        this.employer = employer;
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
