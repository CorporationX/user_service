package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserFilterDtoValidator;
import school.faang.user_service.validator.UserValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

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
}