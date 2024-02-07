package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.user.UserValidator;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    //TODO добавить возврат метода
    public void deactivationUserById(long userId) {
        userValidator.ifUserIdIsValid(userId);
        userService.deactivationUserById(userId);
    }
}
