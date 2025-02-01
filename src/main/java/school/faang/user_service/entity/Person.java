package school.faang.user_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentID;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String faculty;
    private Integer yearOfStudy;
    private String major;
    @JsonProperty("GPA")
    private Double GPA;
    private String status;
    private String admissionDate;
    private String graduationDate;
    private String degree;
    private String institution;
    private Integer completionYear;
    private Boolean scholarship;
    private String employer;
}
