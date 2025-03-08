package school.faang.user_service.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.DeactivatedUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserProfileDto;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            return userService.getPremiumUsers();
        }
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<DeactivatedUserDto> deactivateUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PutMapping("/{userId}/telegram")
    public ResponseEntity<Void> updateTelegramChatId(
            @PathVariable long userId, @RequestParam("chatId") Long telegramChatId) {
        userService.updateTelegramChatId(userId, telegramChatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/profile")
    public UserProfileDto getUserProfile(@PathVariable long userId) {
        return userService.getUserProfile(userId);
    }
}
