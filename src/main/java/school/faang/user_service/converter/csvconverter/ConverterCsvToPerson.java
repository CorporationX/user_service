package school.faang.user_service.converter.csvconverter;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ConverterCsvToPerson {

    public List<Person> convertCsvToPerson(MultipartFile file) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        try {
            MappingIterator<Map<String, String>> mapMappingIterator = csvMapper
                    .readerFor(Map.class)
                    .with(csvSchema)
                    .readValues(file.getInputStream());
            List<Map<String, String>> mapData = mapMappingIterator.readAll();
            return mapDataToPersons(mapData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Person> convertorToPerson(MultipartFile file) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper.schemaFor(PersonSchema.class).withHeader().withColumnReordering(true);
        List<Person> persons = new ArrayList<>();
        try {
            MappingIterator<Person> mappingIterator = csvMapper
                    .readerFor(Person.class)
                    .with(csvSchema)
                    .readValues(file.getInputStream());
            persons.addAll(mappingIterator.readAll());
            return persons;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Person> mapDataToPersons(List<Map<String, String>> mapData) {
        List<Person> persons = new ArrayList<>();
        for (Map<String, String> row : mapData) {
            List<PreviousEducation> previousEducations = new ArrayList<>();
            PreviousEducation previousEducation = new PreviousEducation()
                    .withDegree(row.get("degree"))
                    .withInstitution(row.get("institution"))
                    .withCompletionYear(Integer.parseInt(row.get("completionYear")));
            previousEducations.add(previousEducation);

            Person person = new Person()
                    .withFirstName(row.get("firstName"))
                    .withLastName(row.get("lastName"))
                    .withYearOfBirth(Integer.parseInt(row.get("yearOfBirth")))
                    .withGroup(row.get("group"))
                    .withStudentID(row.get("studentID"))
                    .withContactInfo(new ContactInfo()
                            .withEmail(row.get("email"))
                            .withPhone(row.get("phone"))
                            .withAddress(new Address()
                                    .withStreet("street")
                                    .withCity("city")
                                    .withState("state")
                                    .withCountry("country")
                                    .withPostalCode("postalCode")))
                    .withEducation(new Education()
                            .withFaculty("faculty")
                            .withYearOfStudy(Integer.parseInt(row.get("yearOfStudy")))
                            .withMajor(row.get("major"))
                            .withGpa(Double.parseDouble(row.get("GPA"))))
                    .withStatus(row.get("status"))
                    .withAdmissionDate(row.get("admissionDate"))
                    .withGraduationDate(row.get("graduationDate"))
                    .withPreviousEducation(previousEducations)
                    .withScholarship(Boolean.parseBoolean(row.get("scholarship")))
                    .withEmployer(row.get("employer"));
            persons.add(person);
        }
        return persons;
    }
}
