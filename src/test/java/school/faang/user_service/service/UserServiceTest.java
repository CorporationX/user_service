package school.faang.user_service.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Address;
import com.json.student.ContactInfo;
import com.json.student.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService CSV Processing Tests")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CsvMapper csvMapper;

    @Mock
    private PersonToUserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private InputStream createMockInputStream() {
        String csvData = "firstName,lastName,email,phone,country\n" + "John,J,john.j@example.com,1234567890,USA";
        return new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should successfully process a valid CSV")
    public void testProcessCsv_ValidInput() throws Exception {
        setupMockBehaviorForValidInput();
        InputStream inputStream = createMockInputStream();
        userService.processCsv(inputStream);

        verify(csvMapper, times(1)).readerFor(Person.class);
    }

    @Test
    @DisplayName("Should fail while parsing CSV and never save anything to repository")
    public void testProcessCsv_ExceptionHandling() {
        InputStream inputStream = createMockInputStream();
        userService.processCsv(inputStream);
        verify(userRepository, never()).save(any(User.class));
    }

    private void setupMockBehaviorForValidInput(){
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        when(csvMapper.schemaFor(Person.class)).thenReturn(schema);

        Address address = new Address();
        address.setCountry("USA");

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail("john.j@example.com");
        contactInfo.setPhone("1234567890");
        contactInfo.setAddress(address);

        Person mockPerson = new Person();
        mockPerson.setFirstName("John");
        mockPerson.setLastName("J");
        mockPerson.setContactInfo(contactInfo);

        Country country = new Country();
        country.setId(1L);
        country.setTitle("USA");

        User user = new User();
        user.setUsername(userMapper.combineName(mockPerson.getFirstName(), mockPerson.getLastName()));
        user.setEmail(contactInfo.getEmail());
        user.setPhone(contactInfo.getPhone());
        user.setCountry(country);
        user.setPassword("password");

        var objectReader = Mockito.mock(com.fasterxml.jackson.databind.ObjectReader.class);
        when(csvMapper.readerFor(Person.class)).thenReturn(objectReader);
    }
}