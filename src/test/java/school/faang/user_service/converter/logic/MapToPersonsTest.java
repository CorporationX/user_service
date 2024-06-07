package school.faang.user_service.converter.logic;

import com.json.student.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.converter.component.SetContactInfo;
import school.faang.user_service.converter.component.SetEducation;
import school.faang.user_service.converter.component.SetPerson;
import school.faang.user_service.converter.component.SetPreviousEducation;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class MapToPersonsTest {

    @Test
    public void testMapToPersons() {
        List<Person> persons = getPeople();

        Assertions.assertNotNull(persons);
        Assertions.assertEquals(1, persons.size());
        Person person = persons.get(0);
        Assertions.assertEquals("John", person.getFirstName());
        Assertions.assertEquals("Doe", person.getLastName());
        Assertions.assertEquals(Optional.of(1990), Optional.ofNullable(person.getYearOfBirth()));
        Assertions.assertEquals("A", person.getGroup());
        Assertions.assertEquals("123456", person.getStudentID());
        Assertions.assertEquals("active", person.getStatus());
        Assertions.assertEquals("01/01/2018", person.getAdmissionDate());
        Assertions.assertEquals("01/01/2022", person.getGraduationDate());
        Assertions.assertEquals("Acme Corp", person.getEmployer());
        ContactInfo contactInfo = person.getContactInfo();
        Assertions.assertNotNull(contactInfo);
        Assertions.assertEquals("john.doe@example.com", contactInfo.getEmail());
        Assertions.assertEquals("123-456-7890", contactInfo.getPhone());
        Address address = contactInfo.getAddress();
        Assertions.assertNotNull(address);
        Assertions.assertEquals("123 Main St", address.getStreet());
        Assertions.assertEquals("Anytown", address.getCity());
        Assertions.assertEquals("State", address.getState());
        Assertions.assertEquals("Country", address.getCountry());
        Assertions.assertEquals("12345", address.getPostalCode());
        Education education = person.getEducation();
        Assertions.assertNotNull(education);
        Assertions.assertEquals("Engineering", education.getFaculty());
        Assertions.assertEquals(Optional.of(4), Optional.ofNullable(education.getYearOfStudy()));
        Assertions.assertEquals("Computer Science", education.getMajor());
        Assertions.assertEquals(Optional.of(3.8), Optional.ofNullable(education.getGpa()));
        List<PreviousEducation> previousEducations = person.getPreviousEducation();
        Assertions.assertNotNull(previousEducations);
        Assertions.assertEquals(1, previousEducations.size());
        PreviousEducation previousEducation = previousEducations.get(0);
        Assertions.assertEquals("Bachelor", previousEducation.getDegree());
        Assertions.assertEquals("University", previousEducation.getInstitution());
        Assertions.assertEquals(Optional.of(2012), Optional.ofNullable(previousEducation.getCompletionYear()));
    }

    private List<Person> getPeople() {
        SetPreviousEducation setPreviousEducation = new SetPreviousEducation();
        SetContactInfo setContactInfo = new SetContactInfo();
        SetEducation setEducation = new SetEducation();
        SetPerson setPerson = new SetPerson();
        MapToPerson mapToPerson = new MapToPerson(setPreviousEducation, setContactInfo, setEducation, setPerson);

        List<Map<String, String>> csvData = new ArrayList<>();
        Map<String, String> rowData = new HashMap<>();
        rowData.put("firstName", "John");
        rowData.put("lastName", "Doe");
        rowData.put("yearOfBirth", "1990");
        rowData.put("group", "A");
        rowData.put("studentID", "123456");
        rowData.put("status", "active");
        rowData.put("admissionDate", "01/01/2018");
        rowData.put("graduationDate", "01/01/2022");
        rowData.put("scholarship", "true");
        rowData.put("employer", "Acme Corp");
        rowData.put("email", "john.doe@example.com");
        rowData.put("phone", "123-456-7890");
        rowData.put("street", "123 Main St");
        rowData.put("city", "Anytown");
        rowData.put("state", "State");
        rowData.put("country", "Country");
        rowData.put("postalCode", "12345");
        rowData.put("faculty", "Engineering");
        rowData.put("yearOfStudy", "4");
        rowData.put("major", "Computer Science");
        rowData.put("GPA", "3.8");
        rowData.put("degree", "Bachelor");
        rowData.put("institution", "University");
        rowData.put("completionYear", "2012");
        csvData.add(rowData);

        return mapToPerson.mapToPersons(csvData);
    }
}
