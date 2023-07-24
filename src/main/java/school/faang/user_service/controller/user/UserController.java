package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  public void deactivateUser(Long id) {
    if (id == null) {
      throw new DataValidationException("User ID is required!");
    }

    userService.deactivateUser(id);
  }
}
