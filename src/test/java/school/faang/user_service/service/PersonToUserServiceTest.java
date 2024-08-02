package school.faang.user_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonToUserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PersonMapper personMapperMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private ObjectMapper objectMapperMock;

    InputStream testInputStream = new ByteArrayInputStream("test data".getBytes());
    private final Person testPerson = new Person();
    private final User testUser = new User();
    private final UserDto testUserDto = new UserDto();
    private final Country testCountry = new Country();

    static class ListType$1 extends TypeReference<List<Person>> {
    }

    private Person prepareTestingPerson() {
        testPerson.setFirstName("Kate");
        testPerson.setLastName("Braun");
        return testPerson;
    }

    private User prepareTestingUser() {
        Country userCountry = prepareTestingCountry();
        testUser.setUsername("KateBraun");
        testUser.setEmail("kate@example.com");
        testUser.setPhone("1112223333");
        testUser.setCountry(userCountry);
        return testUser;
    }

    private UserDto prepareTestingUserDto() {
        testUserDto.setUsername("KateBraun");
        testUserDto.setEmail("kate@example.com");
        return testUserDto;
    }

    private Country prepareTestingCountry() {
        testCountry.setTitle("Canada");
        return testCountry;
    }

    @Test
    public void testSaveUsersIfUsernameAlreadyExist() throws IOException {
        User user = prepareTestingUser();
        UserDto userDto = prepareTestingUserDto();
        when(personMapperMock.toUser(any())).thenReturn(user);
        when(userRepository.existsByUsername(any())).thenReturn(true);
        when(userMapperMock.toDtoList(anyList())).thenReturn(List.of(userDto));

        userService.saveUsers(testInputStream);

        verify(userRepository, times(0))
                .save(prepareTestingUser());
    }

    @Test
    public void testSaveUsersIfEmailAlreadyExist() throws IOException {
        User user = prepareTestingUser();
        UserDto userDto = prepareTestingUserDto();
        //Person person = prepareTestingPerson();
        //List<Person> persons = new ArrayList<>();
        //persons.add(person);
        when(personMapperMock.toUser(any())).thenReturn(user);
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userMapperMock.toDtoList(anyList())).thenReturn(List.of(userDto));
////        when(objectMapperMock.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))
////                .thenReturn(objectMapperMock);
//        when(objectMapperMock
//                .readValue(new File("src/main/resources/json/person.json"),
//                        ListType$1.class
//                        //new TypeReference<List<Person>>() {
//                        )).thenReturn((ListType$1) persons);

        userService.saveUsers(testInputStream);

        verify(userRepository, times(0))
                .save(prepareTestingUser());
    }

    @Test
    public void testSaveUsersIfPhoneAlreadyExist() throws IOException {
        User user = prepareTestingUser();
        UserDto userDto = prepareTestingUserDto();
        when(personMapperMock.toUser(any())).thenReturn(user);
        when(userRepository.existsByPhone(any())).thenReturn(true);
        when(userMapperMock.toDtoList(anyList())).thenReturn(List.of(userDto));

        userService.saveUsers(testInputStream);

        verify(userRepository, times(0))
                .save(prepareTestingUser());
    }

    @Test
    public void testSaveUsersSuccessful() throws IOException {
        User user = prepareTestingUser();
        UserDto userDto = prepareTestingUserDto();
        when(personMapperMock.toUser(any())).thenReturn(user);
        when(userMapperMock.toDtoList(anyList())).thenReturn(List.of(userDto));

        userService.saveUsers(testInputStream);

        verify(userRepository, times(4))
                .save(prepareTestingUser());
    }

    @Test
    public void testSaveUsersIfCountryAlreadyExists() throws IOException {
        User user = prepareTestingUser();
        Country country = prepareTestingCountry();
        UserDto userDto = prepareTestingUserDto();
        when(personMapperMock.toUser(any())).thenReturn(user);
        when(countryRepository.existsCountryByTitle(any())).thenReturn(true);
        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(userMapperMock.toDtoList(anyList())).thenReturn(List.of(userDto));

        userService.saveUsers(testInputStream);

        verify(countryRepository, times(0))
                .save(country);
    }

    @Test
    public void testSaveUsersIfCountryDoesNotExists() throws IOException {
        User user = prepareTestingUser();
        Country country = prepareTestingCountry();
        UserDto userDto = prepareTestingUserDto();
        when(personMapperMock.toUser(any())).thenReturn(user);
        when(countryRepository.existsCountryByTitle(any())).thenReturn(false);
        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(userMapperMock.toDtoList(anyList())).thenReturn(List.of(userDto));

        userService.saveUsers(testInputStream);

        verify(countryRepository, times(4))
                .save(country);
    }
}
