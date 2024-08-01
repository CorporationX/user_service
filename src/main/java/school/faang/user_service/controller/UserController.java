package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.ResourceDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    // вынеси в application
    private static final int FILE_LIMIT = 5000000;

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> userIds) {
        return userService.findUsersByIds(userIds);
    }

    @PostMapping("usersPic/{userId}")
    public ResourceDto addUsersPic(@PathVariable long userId, @RequestBody MultipartFile file) throws FileSizeLimitExceededException {
        if (file.getSize() > FILE_LIMIT) {
            throw new FileSizeLimitExceededException("File Size Limit ", file.getSize(), FILE_LIMIT);
        }
        return userService.addUserPic(userId, file);
    }

    @GetMapping("usersPic/{userId}")
    public ResourceDto getUserPic(@PathVariable long userId) {
        return null;
    }

    @DeleteMapping("usersPic/{userId}")
    public void deleteUserPic(@PathVariable long userId) {

    }
}
