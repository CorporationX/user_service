package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private static final String MESSAGE_INVALID_ID = "userId cannot be less than zero";
    private final UserService service;

    @PutMapping("/user/{userId}")
    public UserDto deactivatesUserProfile(@PathVariable Long userId) {
        if (userId < 0) {
            throw new RuntimeException(MESSAGE_INVALID_ID);
        }
        return service.deactivatesUserProfile(userId);
    }
}
