package school.faang.user_service.controller.user;

import com.json.student.Person;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import school.faang.user_service.converter.starter.ConverterCsvToPerson;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorResponse;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserFilterDtoValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Mock
    private UserValidator userValidator;

    @Mock
    private MultipartFile multipartFile;

    private UserFilterDto userFilterDto;
    private Long id;

    @BeforeEach
    public void setUp() {
        userFilterDto = new UserFilterDto();
        id = 1L;
    }

    @Test
    public void testGetPremiumUsers_NullDto() {
        userFilterDto = null;
        assertThrows(IllegalArgumentException.class, () -> userController.getPremiumUsers(userFilterDto));
    }

    @Test
    public void testGetPremiumUsers_IsRunGetPremiumUsers() {
        userController.getPremiumUsers(userFilterDto);
        verify(userService, times(1)).getPremiumUsers(userFilterDto);
    }

    @Test
    public void testSavePic() {
        userController.uploadProfilePicture(id, multipartFile);
        verify(userValidator, times(1)).checkUserInDB(id);
        verify(userValidator, times(1)).checkMaxSizePic(multipartFile);
        verify(userService, times(1)).uploadProfilePicture(id, multipartFile);
    }

    @Test
    public void testGetPic() {
        userController.downloadProfilePicture(id);
        verify(userValidator, times(1)).checkUserInDB(id);
        verify(userService, times(1)).downloadProfilePicture(id);
    }

    @Test
    public void deletePicTest() {
        userController.deleteProfilePicture(id);
        verify(userValidator, times(1)).checkUserInDB(id);
        verify(userService, times(1)).deleteProfilePicture(id);
    }

    @Test
    void testEmptyFile() {
        MultipartFile file = new MockMultipartFile("file", "".getBytes());
        doThrow(DataValidationException.class).when(userValidator).validateCsvFile(file);
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
        doNothing().when(userValidator).validateCsvFile(file);
        userController.convertScvFile(file);

        verify(converterCsvToPerson, times(1)).convertCsvToPerson(file);
        verify(userService, times(1)).convertCsvFile(persons);
    }

    @Test
    public void testAuthorizeUser() {
        userController.authorizeUser("email", "password");

        verify(userService, times(1)).authorizeUser("email", "password");
    }

    @Test
    public void testGetAllUsersId() {
        assertDoesNotThrow(() -> userController.getAllUsersId());
        verify(userService).getAllUsersId();
    }

    @Test
    public void testGetUserByPostId() {
        assertDoesNotThrow(() -> userController.getUserByPostId(12L));
        verify(userService).getUserByPostId(12L);
    }
}