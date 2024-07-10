package school.faang.user_service.converter.component;

import com.json.student.Person;
import org.springframework.stereotype.Component;

@Component
public class SetPerson {
    public Person setPerson(String firstName, String lastName, int yearOfBirth, String group, String studentID,
                          String status, String admissionDate, String graduationDate, boolean scholarship, String employer) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setYearOfBirth(yearOfBirth);
        person.setGroup(group);
        person.setStudentID(studentID);
        person.setStatus(status);
        person.setAdmissionDate(admissionDate);
        person.setGraduationDate(graduationDate);
        person.setScholarship(scholarship);
        person.setEmployer(employer);

        return person;
    }
}
