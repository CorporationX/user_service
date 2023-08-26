package school.faang.user_service.csv_parser.CsvToPerson;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.person_dto.*;
import school.faang.user_service.exception.DataValidException;

import java.util.List;

@Component
public class CsvToPerson {
    public UserPersonDto getPerson(String line) throws DataValidException {
        String[] personArr = line.split(",");
        if (personArr.length != 24) throw new DataValidException("the number of columns does not match");

        UserPersonDto person = new UserPersonDto();
        EducationDto education = new EducationDto();
        ContactInfoDto contactInfo = new ContactInfoDto();
        AddressDto address = new AddressDto();
        PreviousEducationDto previousEducation = new PreviousEducationDto();

        person.setUsername(personArr[0] + personArr[1]);
        person.setFirstName(personArr[0]);
        person.setLastName(personArr[1]);
        person.setYearOfBirth(personArr[2].matches("\\d{4}") ? Integer.valueOf(personArr[2]) : null);
        person.setGroup(personArr[3]);
        person.setStudentID(personArr[4]);

        contactInfo.setEmail(personArr[5]);
        contactInfo.setPhone(personArr[6]);
        address.setStreet(personArr[7]);
        address.setCity(personArr[8]);
        address.setState(personArr[9]);
        address.setCountry(personArr[10]);
        address.setPostalCode(personArr[11]);
        contactInfo.setAddress(address);
        person.setContactInfo(contactInfo);

        education.setFaculty(personArr[12]);
        education.setYearOfStudy(personArr[13].matches("[1-6]|\\d{4}") ? Integer.valueOf(personArr[13]) : null);
        education.setMajor(personArr[14]);
        education.setGpa(personArr[15].matches("[1-5].\\d") ? Double.valueOf(personArr[15]) : null);
        person.setEducation(education);

        person.setStatus(personArr[16]);
        person.setAdmissionDate(personArr[17]);
        person.setGraduationDate(personArr[18]);

        previousEducation.setDegree(personArr[19]);
        previousEducation.setInstitution(personArr[20]);
        previousEducation.setCompletionYear(personArr[21].matches("\\d{4}") ? Integer.valueOf(personArr[21]) : null);
        person.setPreviousEducation(List.of(previousEducation));

        person.setScholarship(Boolean.parseBoolean(personArr[22]));
        person.setEmployer(personArr[23]);
        return person;
    }
}
