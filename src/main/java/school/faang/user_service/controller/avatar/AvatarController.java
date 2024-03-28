package school.faang.user_service.controller.avatar;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.avatar.AvatarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AvatarController {
    private final AvatarService avatarService;
    @Value("${services.s3.max_size}")
    private int maxSize;

    @PostMapping("/{userId}/avatar")
    @Operation(summary = "Upload new avatar for user")
    public UserProfilePic uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        if (file.getSize() > maxSize){
            throw new IllegalArgumentException("File weight is more than 5 MB");
        }
        return avatarService.uploadAvatar(userId, file);
    }

    @GetMapping("/{userId}/avatar")
    @Operation(summary = "Upload new avatar for user")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long userId) {
        return avatarService.getAvatar(userId);
    }

    @DeleteMapping("/{userId}/avatar")
    @Operation(summary = "Delete avatar for user")
    public void deleteAvatar(@PathVariable Long userId) {
        avatarService.deleteAvatar(userId);
    }
}
