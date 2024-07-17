package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private static final long INVALID_ID_FOR_USER = -1L;
    private static final long VALID_USER_ID = 5L;
    @Mock
    private UserService service;
    @InjectMocks
    private UserController controller;

    @Test
    public void testUserDtoIsNull() {
        assertThrows(
                RuntimeException.class,
                () -> controller.deactivatesUserProfile(INVALID_ID_FOR_USER)
        );
    }

    @Test
    public void testVerifyServiceDeactivatesUserProfile() {
        controller.deactivatesUserProfile(VALID_USER_ID);
        Mockito.verify(service).deactivatesUserProfile(Mockito.anyLong());
    }
}