package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/get-by-ids")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        validateUsersIds(ids);
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/send-a-file")
    public String sendAfile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "";
        }

        try {
            userService.registerAnArrayOfUser(file.getInputStream());
        } catch (IOException e) {

        }

        return "";
    }

    private static List<Long> validateUsersIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ids;
    }
}
