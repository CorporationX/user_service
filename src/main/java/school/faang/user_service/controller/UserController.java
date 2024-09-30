package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.user.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public void deactivateUser(Long userId) {
        userService.deactivateUser(userId);
    }
}
