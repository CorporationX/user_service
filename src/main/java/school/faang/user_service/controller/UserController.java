package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    public static final String USER_ID_PATH = "/{userId}";

    private UserService userService;

    @PutMapping(USER_ID_PATH)
    public void deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
    }
}
