package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.userDto.UserPremiumDto;
import school.faang.user_service.dto.userDto.UserFilterDto;
import school.faang.user_service.service.UserPremiumService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserPremiumService userPremiumService;

    @Test
    void testGetListPremiumUsersNull() {
        assertThrows(IllegalArgumentException.class, () -> userController.getListPremiumUsers(null));
    }

    @Test
    void testGetListPremiumUsersTrue() {
        Mockito.when(userPremiumService.getPremiumUsers(Mockito.any())).thenReturn(List.of(new UserPremiumDto()));

        userController.getListPremiumUsers(new UserFilterDto());

        Mockito.verify(userPremiumService, Mockito.times(1)).getPremiumUsers(new UserFilterDto());
    }
}