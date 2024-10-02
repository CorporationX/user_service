package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.service.user.ConvertToUserService;
import school.faang.user_service.service.user.CountryService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConvertToUserServiceTest {

    @Mock
    PersonMapper personMapperMock;
    @Mock
    CountryService countryService;
    @Mock
    UserService userService;
    @Mock
    UserValidator userValidator;
    @InjectMocks
    ConvertToUserService convertToUserService;

    private final Person testPerson = new Person();
    private final User testUser = new User();
    private final Country testCountry = new Country();

    private User prepareTestingUser() {
        Country userCountry = prepareTestingCountry();
        testUser.setUsername("KateBraun");
        testUser.setEmail("kate@example.com");
        testUser.setPhone("1112223333");
        testUser.setCountry(userCountry);
        return testUser;
    }

    private Country prepareTestingCountry() {
        testCountry.setTitle("Canada");
        return testCountry;
    }

    @Test
    public void testPrepareAndSaveUsersIfUsernameOrEmailOrPhoneAlreadyExist() {
        User savedUser = prepareTestingUser();
        when(personMapperMock.toUser(any())).thenReturn(savedUser);
        when(userValidator.validateUserBeforeSave(any())).thenReturn(true);

        convertToUserService.prepareAndSaveUsers(List.of(testPerson));

        verify(userService, times(0))
                .saveUsers(List.of(savedUser));
    }

    @Test
    public void testPrepareAndSaveUsersIfCountryAlreadyExists() {
        User savedUser = prepareTestingUser();
        Country country = prepareTestingCountry();
        when(personMapperMock.toUser(any())).thenReturn(savedUser);
        when(countryService.existsCountryByTitle(any())).thenReturn(true);
        when(countryService.findAllCountries()).thenReturn(List.of(country));

        convertToUserService.prepareAndSaveUsers(List.of(testPerson));

        verify(countryService, times(0))
                .createCountries(List.of(country));
    }

    @Test
    public void testPrepareAndSaveUsersIfCountryDoesNotExists() {
        User savedUser = prepareTestingUser();
        Country country = prepareTestingCountry();
        when(personMapperMock.toUser(any())).thenReturn(savedUser);
        when(countryService.existsCountryByTitle(any())).thenReturn(false);
        when(countryService.findAllCountries()).thenReturn(List.of(country));

        convertToUserService.prepareAndSaveUsers(List.of(testPerson));

        verify(countryService, times(1))
                .createCountries(List.of(country));
    }

    @Test
    public void testPrepareAndSaveUsersSuccessful() {
        User savedUser = prepareTestingUser();
        Country country = prepareTestingCountry();
        when(personMapperMock.toUser(any())).thenReturn(savedUser);
        when(countryService.existsCountryByTitle(any())).thenReturn(true);
        when(countryService.findAllCountries()).thenReturn(List.of(country));

        convertToUserService.prepareAndSaveUsers(List.of(testPerson));

        verify(userService, times(1))
                .saveUsers(List.of(savedUser));
    }
}
