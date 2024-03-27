package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.resource.ResourceDto;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.service.user.UserAvatarService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/avatar")
@Tag(name = "User Avatar Controller", description = "Endpoints for managing users' avatars")
public class UserAvatarController {
    private final UserContext userContext;
    private final UserAvatarService userAvatarService;

    @Operation(summary = "Upload an avatar for user")
    @PostMapping
    public List<ResourceDto> upload(@RequestParam("avatar") MultipartFile avatar) {
        long userId = userContext.getUserId();
        return userAvatarService.upload(userId, avatar);
    }

    @Operation(summary = "Get user's avatar")
    @GetMapping("/{avatarId}")
    public ResponseEntity<byte[]> get(@PathVariable long avatarId) {
        byte[] avatar;
        try {
            avatar = userAvatarService.get(avatarId).readAllBytes();
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            throw new FileException(exception.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(avatar, headers, HttpStatus.OK);
    }

    @Operation(summary = "Delete user's avatar")
    @DeleteMapping
    public ResponseEntity<String> delete() {
        long userId = userContext.getUserId();
        return userAvatarService.delete(userId);
    }
}
