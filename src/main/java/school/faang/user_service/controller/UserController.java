package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.user.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private static final String JSON_RESPONSE_TEMPLATE = "{\"userId\":\"%s\", \"avatarUrl\":\"%s\"}";
    private static final String GENERATED_USERID_LOG = "Generated user ID: {}";
    private static final String GENERATED_AVATAR_LOG = "Generated avatar URL for user ID {}: {}";

    private final UserService userService;
    private final AvatarService avatarService;

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) {
        log.info("Received request to get user with ID {}", userId);
        return userService.getUser(userId);
    }


    @PostMapping("/{userId}")
    public ResponseEntity<?> registerUser(@PathVariable Long userId) {
        log.info(GENERATED_USERID_LOG, userId);

        String avatarUrl = avatarService.generateAndUploadAvatar(String.valueOf(userId));
        log.info(GENERATED_AVATAR_LOG, userId, avatarUrl);

        String jsonResponse = String.format(JSON_RESPONSE_TEMPLATE, userId, avatarUrl);
        return ResponseEntity.ok().body(jsonResponse);
    }
}
