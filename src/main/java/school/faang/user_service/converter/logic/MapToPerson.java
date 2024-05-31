package school.faang.user_service.converter.logic;

import com.json.student.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MapToPerson {

    public List<Person> mapToPersons(List<Map<String, String>> csvData) {
        List<Person> persons = new ArrayList<>();

        for (Map<String, String> row : csvData) {
            Person person = new Person();
            person.setFirstName(row.get("firstName"));
            person.setLastName(row.get("lastName"));
            person.setYearOfBirth(Integer.parseInt(row.get("yearOfBirth")));
            person.setGroup(row.get("group"));
            person.setStudentID(row.get("studentID"));
            person.setStatus(row.get("status"));
            person.setAdmissionDate(row.get("admissionDate"));
            person.setGraduationDate(row.get("graduationDate"));
            person.setScholarship(Boolean.parseBoolean(row.get("scholarship")));
            person.setEmployer(row.get("employer"));

            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setEmail(row.get("email"));
            contactInfo.setPhone(row.get("phone"));

            Address address = new Address();
            address.setStreet(row.get("street"));
            address.setCity(row.get("city"));
            address.setState(row.get("state"));
            address.setCountry(row.get("country"));
            address.setPostalCode(row.get("postalCode"));
            contactInfo.setAddress(address);

            person.setContactInfo(contactInfo);

            Education education = new Education();
            education.setFaculty(row.get("faculty"));
            education.setYearOfStudy(Integer.parseInt(row.get("yearOfStudy")));
            education.setMajor(row.get("major"));
            education.setGpa(Double.parseDouble(row.get("GPA")));
            person.setEducation(education);

            List<PreviousEducation> previousEducations = new ArrayList<>();
            PreviousEducation previousEducation = new PreviousEducation();
            previousEducation.setDegree(row.get("degree"));
            previousEducation.setInstitution(row.get("institution"));
            previousEducation.setCompletionYear(Integer.parseInt(row.get("completionYear")));
            previousEducations.add(previousEducation);
            person.setPreviousEducation(previousEducations);

            persons.add(person);
        }

        return persons;
    }
}
