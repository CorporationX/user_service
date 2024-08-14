package school.faang.user_service.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Address;
import com.json.student.ContactInfo;
import com.json.student.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CsvMapper csvMapper;

    @Mock
    private PersonToUserMapper userMapper = Mappers.getMapper(PersonToUserMapper.class);

    @InjectMocks
    private UserService userService;

    private InputStream createMockInputStream() {
        String csvData = "firstName,lastName,email,phone,country\n" + "John,J,john.j@example.com,1234567890,USA";
        return new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testProcessCsv_ValidInput() throws Exception {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        when(csvMapper.schemaFor(Person.class)).thenReturn(schema);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        InputStream inputStream = createMockInputStream();
        when(file.getInputStream()).thenReturn(inputStream);
        Address address = new Address();
        address.setCountry("USA");

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail("john.j@example.com");
        contactInfo.setPhone("1234567890");
        contactInfo.setAddress(address);

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("J");
        person.setContactInfo(contactInfo);

        Country country = new Country();
        country.setId(1L);
        country.setTitle("USA");

        User user = new User();
        user.setUsername(userMapper.combineName(person.getFirstName(), person.getLastName()));
        user.setEmail(contactInfo.getEmail());
        user.setPhone(contactInfo.getPhone());
        user.setCountry(country);
        user.setPassword("password");

        when(userMapper.personToUser(person)).thenReturn(user);

        var objectReader = Mockito.mock(com.fasterxml.jackson.databind.ObjectReader.class);
        when(csvMapper.readerFor(Person.class)).thenReturn(objectReader);

        var iterator = Mockito.mock(com.fasterxml.jackson.databind.MappingIterator.class);
        when(objectReader.readValues(any(InputStream.class))).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(person);

        userService.processCsv(file.getInputStream());

        verify(userRepository, times(1)).save(user);
        verify(countryRepository, times(1)).findAll();
    }
}

