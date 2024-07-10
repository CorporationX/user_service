package school.faang.user_service.converter.logic;

import com.json.student.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.converter.component.SetContactInfo;
import school.faang.user_service.converter.component.SetEducation;
import school.faang.user_service.converter.component.SetPerson;
import school.faang.user_service.converter.component.SetPreviousEducation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MapToPerson {
    private final SetPreviousEducation setPreviousEducation;
    private final SetContactInfo setContactInfo;
    private final SetEducation setEducation;
    private final SetPerson setPerson;

    public List<Person> mapToPersons(List<Map<String, String>> csvData) {
        List<Person> persons = new ArrayList<>();

        for (Map<String, String> row : csvData) {
            Person person = setPerson.setPerson(row.get("firstName"), row.get("lastName"), Integer.parseInt(row.get("yearOfBirth")),
                    row.get("group"), row.get("studentID"), row.get("status"), row.get("admissionDate"), row.get("graduationDate"),
                    Boolean.parseBoolean(row.get("scholarship")), row.get("employer"));

            ContactInfo contactInfo = setContactInfo.setContactInfo(row.get("email"), row.get("phone"), row.get("street"),
                    row.get("city"), row.get("state"), row.get("country"), row.get("postalCode"));
            person.setContactInfo(contactInfo);

            Education education = setEducation.setEducation(row.get("faculty"), Integer.parseInt(row.get("yearOfStudy")), row.get("major"),
                    Double.parseDouble(row.get("GPA")));
            person.setEducation(education);

            List<PreviousEducation> previousEducations = setPreviousEducation.setPreviousEducation(row.get("degree"),
                    row.get("institution"), Integer.parseInt(row.get("completionYear")));
            person.setPreviousEducation(previousEducations);

            persons.add(person);
        }

        return persons;
    }
}
