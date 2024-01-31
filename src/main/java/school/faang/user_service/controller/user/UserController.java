package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping()
    public UserDto createUser(UserDto userDto) {
        return userService.createUser(userDto);
    }
}
