package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private UserController userController;

    @Test
    void testDeactivateUser() {
        Long testId = 1L;

        ResponseEntity<String> response = userController.deactivateUser(testId);

        verify(userServiceImpl, times(1)).deactivateUser(testId);
        String expectedMessage = "User with ID " + testId + "is deactivated";
        assertEquals(expectedMessage, response.getBody());
    }
}
