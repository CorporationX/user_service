package school.faang.user_service.mapper;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.pojo.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class PersonCsvMapper {
    public List<Person> getPersonsFromInputStream(InputStream inputStream) {
        List<Person> people = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                Person.PersonBuilder personBuilder = Person.builder();
                personBuilder.firstName(parts[0].trim());
                personBuilder.lastName(parts[1].trim());
                personBuilder.yearOfBirth(Integer.parseInt(parts[2].trim()));
                personBuilder.group(parts[3].trim());
                personBuilder.studentID(parts[4].trim());

                Address address = new Address(parts[7].trim(), parts[8].trim(), parts[9].trim(), parts[10].trim(),
                        parts[11].trim());
                ContactInfo contactInfo = new ContactInfo(parts[5].trim(), parts[6].trim(), address);

                contactInfo.setAddress(address);
                personBuilder.contactInfo(contactInfo);

                Education education = new Education(parts[12].trim(), Integer.parseInt(parts[13].trim()),
                        parts[14].trim(), Double.parseDouble(parts[15].trim()));
                personBuilder.education(education);

                personBuilder.status(parts[16].trim());
                personBuilder.admissionDate(parts[17].trim());
                personBuilder.graduationDate(parts[18].trim());

                List<PreviousEducation> previousEducationList = new ArrayList<>();
                PreviousEducation previousEducation = new PreviousEducation(parts[19].trim(), parts[20].trim(),
                        Integer.parseInt(parts[21].trim()));
                previousEducationList.add(previousEducation);
                personBuilder.previousEducation(previousEducationList);

                personBuilder.scholarship(Boolean.parseBoolean(parts[22].trim()));
                personBuilder.employer(parts[23].trim());

                people.add(personBuilder.build());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to convert file to Person object");
        }
        return people;
    }
}
