package school.faang.user_service.mapper.csvmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCsvRow {

    // из твоего файла:
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("yearOfBirth")
    private Integer yearOfBirth;

    @JsonProperty("group")
    private String group;

    @JsonProperty("studentId")
    private String studentId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("street")
    private String street;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String region;

    @JsonProperty("country")
    private String country;

    @JsonProperty("postalCode")
    private String zip;

    @JsonProperty("faculty")
    private String faculty;

    @JsonProperty("yearOfStudy")
    private Integer yearOfStudy;

    @JsonProperty("major")
    private String major;

    @JsonProperty("GPA")
    private Double gpa;

    @JsonProperty("status")
    private String status;

    @JsonProperty("admissionDate")
    private String admissionDate;

    @JsonProperty("graduationDate")
    private String graduationDate;

    @JsonProperty("degree")
    private String degreeName;

    @JsonProperty("institution")
    private String institution;

    @JsonProperty("completionYear")
    private Integer degreeYear;

    @JsonProperty("scholarship")
    private Boolean scholarship;

    @JsonProperty("employer")
    private String employer;

    private String prevDegName;
    private String prevInstitution;
    private Integer prevYear;
}

