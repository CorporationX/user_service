package school.faang.user_service.service.user.upload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.common.PasswordCipher;
import school.faang.user_service.common.PasswordGenerator;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserUploadServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private PasswordCipher passwordCipher;

    @InjectMocks
    private UserUploadServiceImpl userUploadService;

    MultipartFile multipartFile;
    List<Country> countries;
    List<User> users;
    Country country;

    @BeforeEach
    public void setUp() {
        multipartFile = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());
        countries = new ArrayList<>();
        countries.addAll(List.of(
                Country.builder().id(1).title("USA").build(),
                Country.builder().id(2).title("France").build()));

        country = Country.builder().title("Poland").build();

        users = Arrays.asList(
                User.builder()
                        .id(1L)
                        .username("John Doe")
                        .email("john.doe@example.com")
                        .country(countries.get(0))
                        .phone("+313131301411")
                        .build(),
                User.builder()
                        .id(2L)
                        .username("Jane Doe")
                        .email("jane.doe@example.com")
                        .country(countries.get(1))
                        .phone("+313131301412")
                        .build(),
                User.builder()
                        .id(3L)
                        .username("Eric Cartman")
                        .email("eric.cartman@example.com")
                        .country(country)
                        .phone("+313131301413")
                        .build()
        );
    }

    @Test
    @DisplayName("Successfully getting all countries")
    public void testGetAllCountriesSuccess() {
        when(countryRepository.findAll()).thenReturn(countries);

        userUploadService.getAllCountries();

        verify(countryRepository).findAll();
    }

    @Test
    @DisplayName("Successfully setting countries to users")
    public void testSetCountriesToUsersSuccess() {
        Set<Country> unsavedCountries = Set.of(Country.builder()
                        .title("USA")
                        .build(),
                Country.builder()
                        .title("France")
                        .build(),
                Country.builder()
                        .title("Poland")
                        .build()
        );
        countries.add(country);
        when(countryRepository.saveAll(unsavedCountries)).thenReturn(countries);

        userUploadService.setCountriesToUsers(users, new HashMap<>());

        verify(countryRepository).saveAll(unsavedCountries);
        assertEquals(unsavedCountries.size(), countries.size());
    }

    @Test
    @DisplayName("Successfully saving users")
    public void testSaveUsersSuccess() {
        when(passwordGenerator.generatePassword(anyString())).thenReturn("password");
        when(passwordCipher.encryptPassword(anyString())).thenReturn("password");

        userUploadService.saveUsers(users);

        verify(userRepository).batchInsertUsers(anyList());
        assertEquals(3, users.size());
        assertNotNull(users.get(0).getId());
    }

    @Test
    public void testSavedAlreadySavedUser() {
        users = List.of(User.builder()
                .username("Stan Marsh")
                .email("stan.marsh@example.com")
                .country(country)
                .phone("+313131301413")
                .build());
        when(passwordGenerator.generatePassword(anyString())).thenReturn("password");
        when(passwordCipher.encryptPassword(anyString())).thenReturn("password");

        userUploadService.saveUsers(users);

        verify(userRepository).batchInsertUsers(anyList());
        assertNull(users.get(0).getId());
    }
}
