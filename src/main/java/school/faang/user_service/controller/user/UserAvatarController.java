package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.service.user.UserAvatarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserAvatarController {

    private final UserAvatarService avatarService;

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<String> uploadAvatar(
            @PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            avatarService.uploadAvatar(userId, file);
            return ResponseEntity.ok("Avatar uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error uploading avatar: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/avatar/large")
    public ResponseEntity<byte[]> downloadLargeAvatar(@PathVariable Long userId) {
        try {
            byte[] imageBytes = avatarService.downloadLargeAvatar(userId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(imageBytes);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Large avatar not found for user " + userId).getBytes());
        }
    }

    @GetMapping("/{userId}/avatar/small")
    public ResponseEntity<byte[]> downloadSmallAvatar(@PathVariable Long userId) {
        try {
            byte[] imageBytes = avatarService.downloadSmallAvatar(userId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(imageBytes);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Small avatar not found for user " + userId).getBytes());
        }
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long userId) {
        try {
            avatarService.deleteAvatar(userId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Avatar not found for user " + userId);
        }
    }
}
