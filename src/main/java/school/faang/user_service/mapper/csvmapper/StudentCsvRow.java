package school.faang.user_service.mapper.csvmapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCsvRow {

    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentId;
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
    private Double gpa;
    private String status;
    private String admissionDate;
    private String graduationDate;
    private String degree;
    private String institution;
    private Integer completionYear;
    private Boolean scholarship;
    private String employer;
}


