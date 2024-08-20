package school.faang.user_service.service.csv;

import school.faang.user_service.com.json.student.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.person.PersonMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CsvUserServiceTest {

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private CsvUserService csvUserService;
    private Person person1;
    private Person person2;

    private User user1;
    private User user2;
    @Mock
    private MockMultipartFile multipartFile;
    private InputStream inputStream;

    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        String csvData = "name,age,email,phone,city,country\n" +
                "John Doe,30,johndoe@example.com,1234567890,New York,USA\n" +
                "Jane Smith,25,janesmith@example.com,9876543210,San Francisco,USA";
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        multipartFile = new MockMultipartFile("file", "students.csv", "text/csv", inputStream);

        person1 = new Person();
        person1.setAdditionalProperty("email", "johndoe@example.com");
        person1.setAdditionalProperty("phone", "1234567890");
        person1.setAdditionalProperty("city", "New York");
        person1.setAdditionalProperty("country", "USA");

        person2 = new Person();
        person2.setAdditionalProperty("email", "janesmith@example.com");
        person2.setAdditionalProperty("phone", "9876543210");
        person2.setAdditionalProperty("city", "San Francisco");
        person2.setAdditionalProperty("country", "USA");

        List<Person> persons = new ArrayList<>();
        persons.add(person1);
        persons.add(person2);


        user1 = new User();
        user1.setId(1L);
        Country country = new Country(1L, "USA", List.of(user1));
        user1.setCountry(country);

        user2 = new User();
        user2.setId(2L);
        Country country2 = new Country(1L, "USA", List.of(user2));
        user2.setCountry(country2);

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        inputStream = new ByteArrayInputStream(csvData.getBytes());
        multipartFile = new MockMultipartFile("file.csv", inputStream);
    }

    @Test
    public void testGetStudentsParsing() throws IOException {
        when(personMapper.personToUser(person1)).thenReturn(user1);
        when(personMapper.personToUser(person2)).thenReturn(user2);
        when(multipartFile.getInputStream()).thenReturn(multipartFile.getInputStream());

        ResponseEntity<String> response = csvUserService.getStudentsParsing(multipartFile);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Users uploaded successfully", response.getBody());

        verify(personMapper, times(1)).personToUser(person1);
        verify(personMapper, times(1)).personToUser(person2);
    }
}