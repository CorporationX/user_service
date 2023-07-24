package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  @Test
  public void throwValidationExceptionTest() {
    assertThrows(DataValidationException.class, () -> {
      userController.deactivateUser(null);
    });
  }

  @Test
  public void deactivateUserTest() {
    userController.deactivateUser(1L);

    Mockito.verify(userService, Mockito.times(1)).deactivateUser(1L);
  }
}
