package school.faang.user_service.pojo.person;

import lombok.Data;

@Data
public class Person {
    private String firstName;
    private String lastName;
    private int yearOfBirth;
    private String group;
    private long studentId;
    private ContactInfo contactInfo;
    private Education education;
    private Status status;
    private boolean scholarShip;
    private String employer;
}
