package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.User.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public UserDto deactivateUser(Long userId) {
        return userService.deactivateUser(userId);
    }
}
