package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserEventDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.notification.UserChatIdUpdateDto;
import school.faang.user_service.dto.notification.UserNotificationDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @PutMapping("/{id}/deactivate")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{id}/notification")
    public UserNotificationDto getUserNotificationDto(@PathVariable long id) {
        return userService.getUserNotificationDtoById(id);
    }

    @PutMapping("/chat")
    public UserNotificationDto updateUserChat(@RequestBody UserChatIdUpdateDto userChatIdUpdateDto) {
        return userService.updateUserChatId(userChatIdUpdateDto);
    }
}