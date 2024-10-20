package school.faang.user_service.util;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.student.Address;
import school.faang.user_service.model.dto.student.ContactInfo;
import school.faang.user_service.model.dto.student.Education;
import school.faang.user_service.model.dto.student.Person;
import school.faang.user_service.util.impl.CsvParserImpl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CsvParserTest {
    @Test
    @DisplayName("test CSV parsing logic")
    void testCsvParsing() {
        ByteArrayInputStream inputStream = getTestStream();
        List<Person> persons = getTestPersonList();
        var csvParser = new CsvParserImpl();
        List<Person> userList = csvParser.getPersonsFromFile(inputStream);

        assertThat(userList).isEqualTo(persons);
    }

    public static @NotNull List<Person> getTestPersonList() {
        List<Person> persons = new ArrayList<>();

        Person johnDoe = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .yearOfBirth(1998)
                .group("A")
                .studentID("123456")
                .contactInfo(ContactInfo.builder()
                        .email("johndoe@example.com")
                        .phone("+1-123-456-7890")
                        .address(Address.builder()
                                .street("123 Main Street")
                                .city("New York")
                                .state("NY")
                                .country("USA")
                                .postalCode("10001")
                                .build())
                        .build())
                .education(Education.builder()
                        .faculty("Computer Science")
                        .yearOfStudy(3)
                        .major("Software Engineering")
                        .build())
                .status("Active")
                .admissionDate("2016-09-01")
                .graduationDate("2020-05-30")
                .previousEducation(new ArrayList<>())
                .scholarship(true)
                .employer("XYZ Technologies")
                .build();

        Person michaelJohnson = Person.builder()
                .firstName("Michael")
                .lastName("Johnson")
                .yearOfBirth(1988)
                .group("Group A")
                .studentID("246813")
                .contactInfo(ContactInfo.builder()
                        .email("michaeljohnson@example.com")
                        .phone("1112223333")
                        .address(Address.builder()
                                .street("Second Street")
                                .city("Miami")
                                .state("FL")
                                .country("USA")
                                .postalCode("67890")
                                .build())
                        .build())
                .education(Education.builder()
                        .faculty("Law")
                        .yearOfStudy(2021)
                        .major("Corporate Law")
                        .build())
                .status("Graduated")
                .admissionDate("2019-01-01")
                .graduationDate("2021-12-31")
                .previousEducation(new ArrayList<>())
                .scholarship(true)
                .employer("ABC Law Firm")
                .build();

        persons.add(johnDoe);
        persons.add(michaelJohnson);
        return persons;
    }

    public static @NotNull ByteArrayInputStream getTestStream() {
        String csvData = "firstName,lastName,yearOfBirth,group,studentID,email,phone,street,city,state,country,postalCode,faculty,yearOfStudy,major,GPA,status,admissionDate,graduationDate,degree,institution,completionYear,scholarship,employer\n" +
                "John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,New York,NY,USA,10001,Computer Science,3,Software Engineering,3.8,Active,2016-09-01,2020-05-30,High School Diploma,XYZ High School,2016,true,XYZ Technologies\n" +
                "Michael,Johnson,1988,Group A,246813,michaeljohnson@example.com,1112223333,Second Street,Miami,FL,USA,67890,Law,2021,Corporate Law,3.7,Graduated,2019-01-01,2021-12-31,Bachelor,DEF University,2019,true,ABC Law Firm\n" ;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        return inputStream;
    }
}
