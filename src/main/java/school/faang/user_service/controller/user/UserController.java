package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PutMapping("/{userId}/deactivate")
    public void deactivateProfile(@PathVariable long userId) {
        userService.deactivateUser(userId);
    }
}