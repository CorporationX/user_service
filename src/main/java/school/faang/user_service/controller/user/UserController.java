package school.faang.user_service.controller.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
@Data
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    @PutMapping("/deactivate")
    public UserDto deactivateUser(@RequestBody UserDto userDto) {
        return userService.deactivateUser(userDto);
    }

    @PostMapping(value = "/filtered")
    public List<UserDto> getFilteredUsers(@RequestBody UserFilterDto filter) {
        long userId = userContext.getUserId();
        return userService.getFilteredUsers(filter, userId);
    }

    @GetMapping(value = "/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + userId);
        }
        return userService.getUser(userId);
    }

    @PostMapping()
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("The file cannot be empty");
        }

        try (InputStream inputStream = file.getInputStream()) {
            userService.processCsvFile(inputStream);
            return ResponseEntity.ok("The file has been successfully uploaded and processed");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading the file");
        }
    }
}