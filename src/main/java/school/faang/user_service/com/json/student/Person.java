
package school.faang.user_service.com.json.student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "firstName",
    "lastName",
    "yearOfBirth",
    "group",
    "studentID",
    "contactInfo",
    "education",
    "status",
    "admissionDate",
    "graduationDate",
    "previousEducation",
    "scholarship",
    "employer"
})
@Generated("jsonschema2pojo")
public class Person {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("firstName")
    @JsonUnwrapped
    private String firstName;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("lastName")
    @JsonUnwrapped
    private String lastName;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("yearOfBirth")
    @JsonUnwrapped
    private Integer yearOfBirth;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("group")
    private String group;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("studentID")
    private String studentID;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("contactInfo")
    @JsonUnwrapped
    private ContactInfo contactInfo;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("education")
    @JsonUnwrapped
    private Education education;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    private String status;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("admissionDate")
    private String admissionDate;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("graduationDate")
    private String graduationDate;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("previousEducation")
    @JsonUnwrapped
    private List<PreviousEducation> previousEducation = new ArrayList<PreviousEducation>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("scholarship")
    private Boolean scholarship;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("employer")
    private String employer;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("yearOfBirth")
    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("yearOfBirth")
    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("group")
    public String getGroup() {
        return group;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("group")
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("studentID")
    public String getStudentID() {
        return studentID;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("studentID")
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("contactInfo")
    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("contactInfo")
    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("education")
    public Education getEducation() {
        return education;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("education")
    public void setEducation(Education education) {
        this.education = education;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("admissionDate")
    public String getAdmissionDate() {
        return admissionDate;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("admissionDate")
    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("graduationDate")
    public String getGraduationDate() {
        return graduationDate;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("graduationDate")
    public void setGraduationDate(String graduationDate) {
        this.graduationDate = graduationDate;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("previousEducation")
    public List<PreviousEducation> getPreviousEducation() {
        return previousEducation;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("previousEducation")
    public void setPreviousEducation(List<PreviousEducation> previousEducation) {
        this.previousEducation = previousEducation;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("scholarship")
    public Boolean getScholarship() {
        return scholarship;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("scholarship")
    public void setScholarship(Boolean scholarship) {
        this.scholarship = scholarship;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("employer")
    public String getEmployer() {
        return employer;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("employer")
    public void setEmployer(String employer) {
        this.employer = employer;
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
        sb.append(Person.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("firstName");
        sb.append('=');
        sb.append(((this.firstName == null)?"<null>":this.firstName));
        sb.append(',');
        sb.append("lastName");
        sb.append('=');
        sb.append(((this.lastName == null)?"<null>":this.lastName));
        sb.append(',');
        sb.append("yearOfBirth");
        sb.append('=');
        sb.append(((this.yearOfBirth == null)?"<null>":this.yearOfBirth));
        sb.append(',');
        sb.append("group");
        sb.append('=');
        sb.append(((this.group == null)?"<null>":this.group));
        sb.append(',');
        sb.append("studentID");
        sb.append('=');
        sb.append(((this.studentID == null)?"<null>":this.studentID));
        sb.append(',');
        sb.append("contactInfo");
        sb.append('=');
        sb.append(((this.contactInfo == null)?"<null>":this.contactInfo));
        sb.append(',');
        sb.append("education");
        sb.append('=');
        sb.append(((this.education == null)?"<null>":this.education));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
        sb.append(',');
        sb.append("admissionDate");
        sb.append('=');
        sb.append(((this.admissionDate == null)?"<null>":this.admissionDate));
        sb.append(',');
        sb.append("graduationDate");
        sb.append('=');
        sb.append(((this.graduationDate == null)?"<null>":this.graduationDate));
        sb.append(',');
        sb.append("previousEducation");
        sb.append('=');
        sb.append(((this.previousEducation == null)?"<null>":this.previousEducation));
        sb.append(',');
        sb.append("scholarship");
        sb.append('=');
        sb.append(((this.scholarship == null)?"<null>":this.scholarship));
        sb.append(',');
        sb.append("employer");
        sb.append('=');
        sb.append(((this.employer == null)?"<null>":this.employer));
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
        result = ((result* 31)+((this.lastName == null)? 0 :this.lastName.hashCode()));
        result = ((result* 31)+((this.admissionDate == null)? 0 :this.admissionDate.hashCode()));
        result = ((result* 31)+((this.education == null)? 0 :this.education.hashCode()));
        result = ((result* 31)+((this.contactInfo == null)? 0 :this.contactInfo.hashCode()));
        result = ((result* 31)+((this.previousEducation == null)? 0 :this.previousEducation.hashCode()));
        result = ((result* 31)+((this.studentID == null)? 0 :this.studentID.hashCode()));
        result = ((result* 31)+((this.firstName == null)? 0 :this.firstName.hashCode()));
        result = ((result* 31)+((this.graduationDate == null)? 0 :this.graduationDate.hashCode()));
        result = ((result* 31)+((this.scholarship == null)? 0 :this.scholarship.hashCode()));
        result = ((result* 31)+((this.employer == null)? 0 :this.employer.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.yearOfBirth == null)? 0 :this.yearOfBirth.hashCode()));
        result = ((result* 31)+((this.group == null)? 0 :this.group.hashCode()));
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Person) == false) {
            return false;
        }
        Person rhs = ((Person) other);
        return (((((((((((((((this.lastName == rhs.lastName)||((this.lastName!= null)&&this.lastName.equals(rhs.lastName)))&&((this.admissionDate == rhs.admissionDate)||((this.admissionDate!= null)&&this.admissionDate.equals(rhs.admissionDate))))&&((this.education == rhs.education)||((this.education!= null)&&this.education.equals(rhs.education))))&&((this.contactInfo == rhs.contactInfo)||((this.contactInfo!= null)&&this.contactInfo.equals(rhs.contactInfo))))&&((this.previousEducation == rhs.previousEducation)||((this.previousEducation!= null)&&this.previousEducation.equals(rhs.previousEducation))))&&((this.studentID == rhs.studentID)||((this.studentID!= null)&&this.studentID.equals(rhs.studentID))))&&((this.firstName == rhs.firstName)||((this.firstName!= null)&&this.firstName.equals(rhs.firstName))))&&((this.graduationDate == rhs.graduationDate)||((this.graduationDate!= null)&&this.graduationDate.equals(rhs.graduationDate))))&&((this.scholarship == rhs.scholarship)||((this.scholarship!= null)&&this.scholarship.equals(rhs.scholarship))))&&((this.employer == rhs.employer)||((this.employer!= null)&&this.employer.equals(rhs.employer))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.yearOfBirth == rhs.yearOfBirth)||((this.yearOfBirth!= null)&&this.yearOfBirth.equals(rhs.yearOfBirth))))&&((this.group == rhs.group)||((this.group!= null)&&this.group.equals(rhs.group))))&&((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status))));
    }

}
