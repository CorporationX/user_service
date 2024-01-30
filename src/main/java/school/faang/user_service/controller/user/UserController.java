package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;

@RestController
@RequestMapping("${endpoint_base.path}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UserDto createUser(UserDto userDto) {
        return userService.createUser(userDto);
    }
}
