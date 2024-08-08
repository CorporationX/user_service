package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/deactivate/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto deactivateUser(@PathVariable Long userId) {
        return userService.deactivateUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return userService.getUserDtoById(userId);
    }

    @GetMapping("/byList")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersDtoByIds(ids);
    }

    @PutMapping("/avatar/{fileId}/{smallFileId}")
    public void uploadAvatar(@RequestHeader(value = "x-user-id") long userId,
                             @PathVariable String fileId,
                             @PathVariable String smallFileId) {
        userService.uploadAvatar(userId, fileId, smallFileId);
    }

    @DeleteMapping("/avatar/delete/{largeFileKey}/{smallFilekey}")
    public void deleteAvatar(@RequestHeader(value = "x-user-id") long userId) {
        userService.deleteAvatar(userId);
    }
}
