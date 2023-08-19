package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.DeactivateResponseDto;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto signup(@RequestBody UserDto userDto) {
        return userService.signup(userDto);
    }

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/premium-users")
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("/get-by-ids")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        validateUsersIds(ids);
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/send-a-file")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("empty file");
        }

        userService.registerAnArrayOfUser(file.getInputStream());
    }

    @PostMapping("/deactivation/{userId}")
    public DeactivateResponseDto deactivating(@PathVariable @Min(0) long userId) {
        return userService.deactivateUser(userId);
    }

    private static List<Long> validateUsersIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ids;
    }
}
