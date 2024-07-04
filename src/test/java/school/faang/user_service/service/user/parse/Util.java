package school.faang.user_service.service.user.parse;

import school.faang.user_service.entity.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Util {

    private static final String HEADER = "firstName,lastName,yearOfBirth,group,studentID,email,phone,street,city,state,country" +
            ",postalCode,faculty,yearOfStudy,major,GPA,status,admissionDate,graduationDate,degree,institution" +
            ",completionYear,scholarship,employer\n";

    private final static String LINE = "John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,New York,NY,USA" +
            ",10001,Computer Science,3,Software Engineering,3.8,Active,2016-09-01,2020-05-30,High School Diploma" +
            ",XYZ High School,2016,true,XYZ Technologies";

    public static InputStream getInputStream() {
        String data = HEADER + LINE;
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }

    public static List<CsvPart> getCsvParts() {
        CsvPart part = new CsvPart();
        part.setLines(List.of(
                HEADER,
                LINE
        ));
        List<CsvPart> parts = new ArrayList<>();
        parts.add(part);
        return parts;
    }

    public static List<Person> getPersons() {
        Person person = new Person();

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail("johndoe@example.com");
        contactInfo.setPhone("+1-123-456-7890");

        Address address = new Address();
        address.setStreet("123 Main Street");
        address.setCity("New York");
        address.setState("NY");
        address.setCountry("USA");
        address.setPostalCode("10001");

        contactInfo.setAddress(address);

        person.setFirstName("John");
        person.setLastName("Doe");
        person.setYearOfBirth(1998);
        person.setGroup("A");
        person.setStudentID("123456");
        person.setContactInfo(contactInfo);

        Education education = new Education();
        education.setFaculty("Computer Science");
        education.setYearOfStudy(3);
        education.setMajor("Software Engineering");
        education.setGpa(3.8F);

        person.setEducation(education);

        person.setStatus("Active");
        person.setAdmissionDate("2016-09-01");
        person.setGraduationDate("2020-05-30");

        List<PreviousEducation> previousEducations = new ArrayList<>();
        PreviousEducation previousEducation = new PreviousEducation();
        previousEducation.setDegree("High School Diploma");
        previousEducation.setInstitution("XYZ High School");
        previousEducation.setCompletionYear(2016);
        previousEducations.add(previousEducation);

        person.setPreviousEducation(previousEducations);

        person.setScholarship(true);
        person.setEmployer("XYZ Technologies");
        return List.of(person);
    }
}
