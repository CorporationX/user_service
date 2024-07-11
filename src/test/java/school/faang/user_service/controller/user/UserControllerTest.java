package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.userDto.UserDto;
import school.faang.user_service.dto.userDto.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    @Test
    void testGetListPremiumUsersNull() {
        assertThrows(NullPointerException.class, () -> userController.getListPremiumUsers(null));
    }

    @Test
    void testGetListPremiumUsersTrue() {
        Mockito.when(userService.getPremiumUsers(Mockito.any())).thenReturn(List.of(new UserDto()));

        userController.getListPremiumUsers(new UserFilterDto());

        Mockito.verify(userService, Mockito.times(1)).getPremiumUsers(new UserFilterDto());
    }
}