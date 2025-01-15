package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.PersonSchemaForUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserCsvDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.ReadFileException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Spy
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @InjectMocks
    UserService userService;
    @Mock
    private UserCountryService countryService;
    @Mock
    private CsvMapper csvMapper;
    @Mock
    private CsvSchema csvSchema;

    @Mock
    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;

    @Spy
    private UserContext userContext;

    @Test
    @DisplayName("Get user by ID successfully")
    void getUserTest() {
        userContext.setUserId(1L);
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .username("username")
                .participatedEvents(List.of(new Event(), new Event()))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto result = userService.getUserDtoByID(userId);
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
    }


    @Test
    @DisplayName("Get user not found by ID")
    void getUserNotFoundTest() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserDtoByID(userId)
        );

        String expectedMessage = String.format(ErrorMessage.USER_NOT_FOUND, userId);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Get users by IDs successfully")
    void getUsersIdsTest() {
        List<Long> userIds = List.of(1L, 2L);
        User user1 = User.builder().id(1L).participatedEvents(List.of(new Event(), new Event())).build();
        User user2 = User.builder().id(2L).participatedEvents(List.of(new Event(), new Event())).build();
        when(userRepository.findAllById(userIds)).thenReturn(List.of(user1, user2));
        List<UserDto> result = userService.getUsersByIds(userIds);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Get no users found by IDs")
    void getUsersNotFoundTest() {
        List<Long> userIds = List.of(1L, 2L);
        when(userRepository.findAllById(userIds)).thenReturn(Collections.emptyList());
        List<UserDto> result = userService.getUsersByIds(userIds);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("User exists by ID is true")
    void existsByIdTrue() {
        userContext.setUserId(1L);
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        assertTrue(userService.isUserExistByID(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    @DisplayName("User exists by ID is false")
    void existsByIdFalse() {
        userContext.setUserId(1L);
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertFalse(userService.isUserExistByID(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    @DisplayName("Save user")
    void saveUser() {
        User user = new User();
        userService.saveUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Get not existing users from empty list")
    void getNotExistingUsersEmptyListTest() {
        List<Long> userIds = Collections.emptyList();
        List<Long> notExistingUserIds = UserService.getNotExistingUserIds(userRepository, userIds);
        assertTrue(notExistingUserIds.isEmpty());
    }

    @Test
    @DisplayName("Get not existing users from valid list")
    void getNotExistingUsersValidListTest() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        when(userRepository.findAllById(userIds)).thenReturn(List.of(User.builder().id(2L).build()));
        List<Long> notExistingUserIds = UserService.getNotExistingUserIds(userRepository, userIds);
        assertEquals(2, notExistingUserIds.size());
        assertTrue(notExistingUserIds.contains(1L));
        assertTrue(notExistingUserIds.contains(3L));
    }

    @Test
    @DisplayName("Create Persons Async When Email Already Exists")
    public void createPersonsAsyncWhenEmailExists() {
        PersonSchemaForUser personOne = new PersonSchemaForUser();
        personOne.setEmail("john.doe@example.com");
        UserCsvDto result = UserCsvDto.builder()
                .email(personOne.getEmail())
                .aboutMe("Already exists")
                .build();
        when(userRepository.existsByEmail(personOne.getEmail())).thenReturn(true);
        List<CompletableFuture<UserCsvDto>> futures = userService.createPersonsAsync(List.of(personOne));
        for (CompletableFuture<UserCsvDto> future : futures) {
            UserCsvDto expected = future.join();
            assertEquals(expected, result);
        }
    }

    @Test
    @DisplayName("Create Multiple Persons Asynchronously")
    void testCreatePersonsAsync() {
        PersonSchemaForUser personOne = new PersonSchemaForUser();
        personOne.setEmail("john.doe@example.com");
        PersonSchemaForUser personTwo = new PersonSchemaForUser();
        personTwo.setEmail("jane.smith@example.com");
        List<PersonSchemaForUser> persons = List.of(personOne, personTwo);
        List<CompletableFuture<UserCsvDto>> createdUsers = userService.createPersonsAsync(persons);
        Assertions.assertNotNull(createdUsers);
        Assertions.assertEquals(2, createdUsers.size());
    }

    @Test
    @DisplayName("Parse CSV File Successfully")
    public void testParseCsv() throws IOException {
        MockMultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", "name,email\njohn,john@example.com\njane,jane@example.com".getBytes());
        ObjectReader testObject = Mockito.mock(ObjectReader.class);
        MappingIterator<Object> iterator = Mockito.mock(MappingIterator.class);
        Mockito.when(csvMapper.readerFor(any(Class.class))).thenReturn(testObject);
        Mockito.when(testObject.with(csvSchema)).thenReturn(testObject);
        Mockito.when(testObject.readValues(any(InputStream.class))).thenReturn(iterator);
        Mockito.when(iterator.readAll()).thenReturn(new ArrayList<>());
        userService.readingUsersFromCsv(csvFile);
        Mockito.verify(csvMapper, Mockito.times(1)).readerFor(PersonSchemaForUser.class);
        Mockito.verify(testObject, Mockito.times(1)).with(csvSchema);
        Mockito.verify(testObject, Mockito.times(1)).readValues(Mockito.any(InputStream.class));
        Mockito.verify(iterator, Mockito.times(1)).readAll();
    }

    @Test
    @DisplayName("Create User from CSV - Successful Creation")
    void testCreateUserFromCsv_SuccessfulCreation() {
        UserCsvDto userCsvDto = new UserCsvDto();
        userCsvDto.setEmail("test@example.com");
        userCsvDto.setCountry(new Country());
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setParticipatedEvents(new ArrayList<>());
        when(countryService.createCountryIfNotExists(any())).thenReturn(new Country());
        when(userMapper.toCsvEntity(userCsvDto)).thenReturn(savedUser);
        when(userRepository.save(any())).thenReturn(savedUser);
        assertDoesNotThrow(() -> userService.createUserFromCsv(userCsvDto));
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Create User from CSV Throws Exception")
    public void testCreateUserCSV_ThrowsException() {
        MockMultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", "name,email\njohn,john@example.com\njane,jane@example.com".getBytes());
        ObjectReader testObject = Mockito.mock(ObjectReader.class);
        when(csvMapper.readerFor(any(Class.class))).thenReturn(testObject);
        when(testObject.with(csvSchema)).thenReturn(testObject);
        assertThrows(Exception.class, () -> userService.readingUsersFromCsv(csvFile));
    }

    @Test
    @DisplayName("Read File Throws ReadFileException")
    void testReadFileException() {
        MockMultipartFile csvFile = new MockMultipartFile("file", "test.csv", "text/csv", "name,email\njohn,john@example.com\njane,jane@example.com".getBytes());
        Assertions.assertThrows(ReadFileException.class, () -> {
            userService.readingUsersFromCsv(csvFile);
        }, "Expected ReadFileException to be thrown");
    }

    @Test
    @DisplayName("Generate Username from Email")
    void testGenerateUsername_WithEmail() {
        UserCsvDto userCsvDto = new UserCsvDto();
        userCsvDto.setEmail("john.doe@example.com");
        String username = invokeGenerateUsername(userCsvDto);
        assertEquals("john.doe", username);
    }

    @Test
    @DisplayName("Generate Random Username When No Email")
    void testGenerateUsername_WithoutEmail() {
        UserCsvDto userCsvDto = new UserCsvDto();
        String username = invokeGenerateUsername(userCsvDto);
        assertTrue(username.startsWith("user_"));
        assertEquals(13, username.length());
    }

    @Test
    @DisplayName("Generate Default Password")
    void testGenerateDefaultPassword() {
        String password = invokeGenerateDefaultPassword();
        assertNotNull(password);
        assertEquals(12, password.length());
        assertTrue(password.matches("^[a-zA-Z0-9]+$"));
    }

    @Test
    @DisplayName("Validate Minimal Required Fields - Valid Person")
    void testValidateMinimalRequiredFields_ValidPerson() {
        PersonSchemaForUser person = new PersonSchemaForUser();
        person.setEmail("valid@example.com");
        boolean result = invokeValidateMinimalRequiredFields(person);
        assertTrue(result);
    }

    @Test
    @DisplayName("Validate Minimal Required Fields - Invalid Person")
    void testValidateMinimalRequiredFields_InvalidPerson() {
        PersonSchemaForUser person = new PersonSchemaForUser();
        person.setEmail("");
        boolean result = invokeValidateMinimalRequiredFields(person);
        assertFalse(result);
    }

    private String invokeGenerateUsername(UserCsvDto userCsvDto) {
        try {
            java.lang.reflect.Method method = UserService.class.getDeclaredMethod("generateUsername", UserCsvDto.class);
            method.setAccessible(true);
            return (String) method.invoke(userService, userCsvDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeGenerateDefaultPassword() {
        try {
            java.lang.reflect.Method method = UserService.class.getDeclaredMethod("generateDefaultPassword");
            method.setAccessible(true);
            return (String) method.invoke(userService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokeValidateMinimalRequiredFields(PersonSchemaForUser person) {
        try {
            java.lang.reflect.Method method = UserService.class.getDeclaredMethod("validateMinimalRequiredFields", PersonSchemaForUser.class);
            method.setAccessible(true);
            return (boolean) method.invoke(userService, person);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}