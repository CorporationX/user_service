package school.faang.user_service.controller.user;

import com.json.student.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.converter.starter.ConverterCsvToPerson;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserFilterDtoValidator;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private ConverterCsvToPerson converterCsvToPerson;

    @Spy
    private UserFilterDtoValidator userFilterDtoValidator;

    private UserFilterDto userFilterDto;

    @BeforeEach
    public void setUp() {
        userFilterDto = new UserFilterDto();
    }

    @Test
    public void getPremiumUsers_NullDtoTest() {
        userFilterDto = null;
        assertThrows(IllegalArgumentException.class, () -> userController.getPremiumUsers(userFilterDto));
    }

    @Test
    public void getPremiumUsers_IsRunGetPremiumUsers() {
        userController.getPremiumUsers(userFilterDto);
        verify(userService, times(1)).getPremiumUsers(userFilterDto);
    }

    @Test
    void testEmptyFile() {
        MultipartFile file = new MockMultipartFile("file", "".getBytes());

        assertThrows(DataValidationException.class, () -> userController.convertScvFile(file));

        verify(converterCsvToPerson, never()).convertCsvToPerson(file);
        verify(userService, never()).convertCsvFile(anyList());
    }

    @Test
    public void testConvertScvFile() {
        MultipartFile file = new MockMultipartFile("file", "Hello, World!".getBytes());

        Person person1 = new Person();
        Person person2 = new Person();
        List<Person> persons = Arrays.asList(person1, person2);

        when(converterCsvToPerson.convertCsvToPerson(file)).thenReturn(persons);

        userController.convertScvFile(file);

        verify(converterCsvToPerson, times(1)).convertCsvToPerson(file);
        verify(userService, times(1)).convertCsvFile(persons);
    }
}