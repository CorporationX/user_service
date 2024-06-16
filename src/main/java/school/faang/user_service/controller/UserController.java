package school.faang.user_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.notification.UserNotificationDto;
import school.faang.user_service.service.user.UserService;
import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/")
    public UserDTO createUser(@RequestBody UserDTO userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable long userId) {
        return userService.findById(userId);
    }

    @GetMapping("/")
    public List<UserDTO> getAll() {
        return userService.findAll();
    }

    @PutMapping("/deactivate/{id}")
    public void deactivateUser(@PathVariable(name = "id") long userId) {
        userService.deactivateUser(userId);
    }

    @GetMapping("/notify/{id}")
    public UserNotificationDto getDtoForNotification(@PathVariable("id") long userId) {
        return userService.getDtoForNotification(userId);
    }
}