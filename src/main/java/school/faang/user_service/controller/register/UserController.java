package school.faang.user_service.controller.register;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.register.UserProfileDto;
import school.faang.user_service.dto.register.UserRegistrationDto;
import school.faang.user_service.service.user.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserProfileDto registerUser(@RequestBody UserRegistrationDto registrationDto,
                                       @RequestHeader(value = "x-user-id") String userId) {
        return userService.registerUser(registrationDto);
    }
}
