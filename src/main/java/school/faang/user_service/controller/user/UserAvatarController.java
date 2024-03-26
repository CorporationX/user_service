package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.resource.ResourceDto;
import school.faang.user_service.service.user.UserAvatarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/avatar")
@Tag(name = "User Avatar Controller", description = "Endpoints for managing users' avatars")
public class UserAvatarController {
    private final UserContext userContext;
    private final UserAvatarService userAvatarService;

    @Operation(summary = "Upload an avatar for user")
    @PostMapping
    public ResourceDto upload(@RequestBody MultipartFile avatar) {
        long userId = userContext.getUserId();
        return userAvatarService.upload(userId, avatar);
    }
}
