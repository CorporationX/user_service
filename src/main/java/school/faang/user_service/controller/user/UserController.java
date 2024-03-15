package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.user.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public void deactivateUser(long userId) {
        userService.deactivateUser(userId);
    }
}
