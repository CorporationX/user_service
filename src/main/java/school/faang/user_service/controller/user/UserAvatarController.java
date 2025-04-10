package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.user.UserAvatarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/avatars")
public class UserAvatarController {

    private final UserAvatarService avatarService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> uploadAvatar(
            @PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        avatarService.uploadAvatar(userId, file);
        return ResponseEntity.ok("Avatar uploaded successfully");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<InputStreamResource> downloadLargeAvatar(@PathVariable Long userId) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(avatarService.downloadLargeAvatar(userId));
    }

    @GetMapping("/{userId}/compressed")
    public ResponseEntity<InputStreamResource> downloadSmallAvatar(@PathVariable Long userId) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(avatarService.downloadSmallAvatar(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long userId) {
        avatarService.deleteAvatar(userId);
        return ResponseEntity.ok("Avatar deleted successfully");
    }
}
