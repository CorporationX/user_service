package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.user.UserValidator;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;

    public void deactivateUser(long userId) {
        userValidator.validateIfUserExistsById(userId);
        userService.deactivateUser(userId);
    }
}
