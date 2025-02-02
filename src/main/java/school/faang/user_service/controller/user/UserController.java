package school.faang.user_service.controller.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.user.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
    if (userFilterDto == null) {
      return userService.getPremiumUsers();
    }
    return userService.getPremiumUsers(userFilterDto);
  }
}
