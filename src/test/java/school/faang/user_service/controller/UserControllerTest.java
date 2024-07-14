package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService service;
    @InjectMocks
    private UserController controller;

    @Test
    public void testUserDtoIsNull() {
        assertThrows(
                RuntimeException.class,
                () -> controller.deactivatesUserProfile(null)
        );
    }

    @Test
    public void testVerifyServiceDeactivatesUserProfile() {
        controller.deactivatesUserProfile(new UserDto());
        Mockito.verify(service).deactivatesUserProfile(Mockito.anyLong());
    }
}