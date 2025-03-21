package school.faang.user_service.controller.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.avatar.AvatarService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/avatar")
public class UserController {
    private static final String JSON_RESPONSE_TEMPLATE = "{\"userId\":\"%s\", \"avatarUrl\":\"%s\"}";
    private static final String RECEIVED_REQUEST_LOG = "Received registration request%s";
    private static final String GENERATED_USERID_LOG = "Generated user ID: {}";
    private static final String GENERATED_AVATAR_LOG = "Generated avatar URL for user ID {}: {}";

    private final AvatarService avatarService;

    @PostMapping()
    public ResponseEntity<?> registerUser(@RequestParam(required = false) String username) {
        if (username != null) {
            log.info(String.format(RECEIVED_REQUEST_LOG, " for username: " + username));
        } else {
            log.info(String.format(RECEIVED_REQUEST_LOG, ""));
        }

        String userId = UUID.randomUUID().toString();
        log.info(GENERATED_USERID_LOG, userId);

        String avatarUrl = avatarService.generateAndUploadAvatar(userId);
        log.info(GENERATED_AVATAR_LOG, userId, avatarUrl);

        String jsonResponse = String.format(JSON_RESPONSE_TEMPLATE, userId, avatarUrl);
        return ResponseEntity.ok().body(jsonResponse);
    }
}
