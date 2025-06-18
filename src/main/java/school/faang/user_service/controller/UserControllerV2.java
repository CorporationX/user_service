package school.faang.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.UserNotificationDto;
import school.faang.user_service.service.UserService;

@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserControllerV2 {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserNotificationDto getUser(@PathVariable Long userId) {
        return userService.getUserNotificationById(userId);
    }
}
