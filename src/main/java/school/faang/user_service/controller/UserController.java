package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> userIds) {
        return userService.findUsersByIds(userIds);
    }
}
