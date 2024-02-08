package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.service.user.UserService;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users/{userId}/deactivate/")
    public void deactivationUserById(@PathVariable long userId) {
        userService.deactivationUserById(userId);
        System.out.println("Error");
    }

}
