package school.faang.user_service.service.csv;

import jakarta.persistence.EntityManager;
import org.mockito.Spy;
import org.springframework.transaction.PlatformTransactionManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.person.PersonMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CsvUserServiceTest {
    @Spy
    private PersonMapperImpl personMapper;
    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CsvUserService csvUserService;
    private MockMultipartFile multipartFile;
    List<String> titleList;
    List<Country> countryList;

    @BeforeEach
    public void setup() throws IOException {
        String csvData = "firstName,lastName,email,phone,city,country\n" +
                "John,Doe,johndoe@example.com,1234567890,New York,USA\n" +
                "Jane,Smith,janesmith@example.com,9876543210,San Francisco,USA";
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        multipartFile = new MockMultipartFile("file", "students.csv", "text/csv", inputStream);

        titleList = List.of("1", "2");

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        Country country1 = new Country();
        country1.setId(1L);
        country1.setTitle("New York");
        country1.setResidents(List.of(user1, user2));

        Country country2 = new Country();
        country2.setTitle("Magadan");
        country2.setId(2L);
        country2.setResidents(List.of(user1, user2));

        countryList = new ArrayList<>();
        countryList.add(country1);
        countryList.add(country2);
    }

    @Test
    public void testGetStudentsParsing() {
        when(countryRepository.findByTitleIn(anyList())).thenReturn(countryList);
        when(userRepository.existsByUsernameOrEmailOrPhone(any(), any(), any())).thenReturn(false);

        ResponseEntity<String> response = csvUserService.getStudentsParsing(multipartFile);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Users uploaded successfully", response.getBody());

        verify(personMapper, times(2)).personToUser(any());
        verify(countryRepository, times(1)).findByTitleIn(anyList());
    }

    @Test
    public void testInsertUser() {
        when(countryRepository.findByTitleIn(anyList())).thenReturn(countryList);
        when(userRepository.existsByUsernameOrEmailOrPhone("John Doe", "johndoe@example.com", "1234567890")).thenReturn(true);

        ResponseEntity<String> response = csvUserService.getStudentsParsing(multipartFile);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Users uploaded successfully", response.getBody());

        verify(personMapper, times(2)).personToUser(any());
        verify(countryRepository, times(1)).findByTitleIn(anyList());
        verify(entityManager, times(1)).persist(any(User.class));
    }
}