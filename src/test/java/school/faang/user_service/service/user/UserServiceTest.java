package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Address;
import school.faang.user_service.entity.person.ContactInfo;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.password.PasswordService;
import school.faang.user_service.validator.user.UserValidator;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CountryService countryService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserValidator userValidator;

    @Mock
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    @InjectMocks
    private UserService userService;

    private Person person;

    @BeforeEach
    void setUp() {
        Address address = new Address();
        address.setCountry("CountryName");

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail("test@example.com");
        contactInfo.setPhone("123456789");
        contactInfo.setAddress(address);

        person = new Person();
        person.setContactInfo(contactInfo);

    }

    private long userId = 1L;
    private User user = User.builder()
            .id(userId)
            .build();

    @Test
    public void testFindNotExistingUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> userService.findUserById(userId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testFindExistingUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User expected = userService.findUserById(userId);
        assertEquals(user, expected);
    }

    @Test
    void testProcessPerson() throws Exception {
        // Mock the user mapping and saving
        User user = new User();
        when(userMapper.personToUser(any(Person.class))).thenReturn(user);
        when(passwordService.generatePassword()).thenReturn("password");
        when(countryService.getCountryOrCreate(anyString())).thenReturn(new Country());

        // Use reflection to access private method
        Method processPersonMethod = UserService.class.getDeclaredMethod("processPerson", Person.class);
        processPersonMethod.setAccessible(true);

        // Call the method
        processPersonMethod.invoke(userService, person);

        // Verify interactions
        verify(userMapper, times(1)).personToUser(person);
        verify(passwordService, times(1)).generatePassword();
        verify(countryService, times(1)).getCountryOrCreate(anyString());
        verify(userRepository, times(1)).save(user);
    }
}
