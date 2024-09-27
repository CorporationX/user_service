package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.service.UserProfilePicService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.user.UserProfilePicServiceImpl;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserProfilePicServiceImpl userProfilePicService;

    @PutMapping("/deactivate/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/{userId}/avatar")
    public void uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        userProfilePicService.uploadAvatar(userId, file);
    }

    @GetMapping("/{userId}/avatar")
    public UserProfilePicDto getAvatar(@PathVariable Long userId) {
        return userProfilePicService.getAvatar(userId);
    }

    @DeleteMapping("/{userId}/avatar")
    public void deleteAvatar(@PathVariable Long userId) {
        userProfilePicService.deleteAvatar(userId);
    }
}
