package school.faang.user_service.controller.avatar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.avatar.AvatarService;

@Tag(name = "user_avatar_methods")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/avatar")
public class UserAvatarController {
    private static final String JSON_RESPONSE_TEMPLATE = "{\"userId\":\"%s\", \"avatarUrl\":\"%s\"}";
    private static final String RECEIVED_REQUEST_LOG = "Received registration request%s";
    private static final String GENERATED_USERID_LOG = "Generated user ID: {}";
    private static final String GENERATED_AVATAR_LOG = "Generated avatar URL for user ID {}: {}";

    private final AvatarService avatarService;

    @Operation(
            summary = "Регистрация пользователя с аватаром",
            description = "Генерирует и загружает аватар для пользователя, используя его ID."
    )
    @PostMapping("/{userId}")
    public ResponseEntity<?> registerUser(@PathVariable Long userId) {
        log.info(GENERATED_USERID_LOG, userId);

        String avatarUrl = avatarService.generateAndUploadAvatar(String.valueOf(userId));
        log.info(GENERATED_AVATAR_LOG, userId, avatarUrl);

        String jsonResponse = String.format(JSON_RESPONSE_TEMPLATE, userId, avatarUrl);
        return ResponseEntity.ok().body(jsonResponse);
    }
}
