package school.faang.user_service.pojo.person;

import lombok.Data;

@Data
public class Education {
    private String faculty;
    private int yearOfStudy;
    private String major;
    private double GPA;
    private String status;
}
