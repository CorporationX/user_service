package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
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

    @PostMapping("/users/upload")
    public List<UserDto> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return userService.uploadFile(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file");
        }
    }
}
