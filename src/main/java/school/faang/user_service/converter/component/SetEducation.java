package school.faang.user_service.converter.component;

import com.json.student.Education;
import org.springframework.stereotype.Component;

@Component
public class SetEducation {
    public Education setEducation(String faculty, int yearOfStudy, String major, double gpa) {
        Education education = new Education();
        education.setFaculty(faculty);
        education.setYearOfStudy(yearOfStudy);
        education.setMajor(major);
        education.setGpa(gpa);

        return education;
    }
}
