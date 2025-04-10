package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.UserAvatarService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/avatars")
public class UserAvatarController {
    private final UserAvatarService userAvatarService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            userAvatarService.uploadAvatar(userId, file);
            return ResponseEntity.ok("Avatar uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error uploading avatar: " + e.getMessage());
        }
    }
}
