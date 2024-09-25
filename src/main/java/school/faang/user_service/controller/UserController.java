package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/deactivate/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }
}
