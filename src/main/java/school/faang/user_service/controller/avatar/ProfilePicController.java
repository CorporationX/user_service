package school.faang.user_service.controller.avatar;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.service.profile_pic.ProfilePicService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class ProfilePicController {
    private final ProfilePicService profilePicService;
    @Value("${services.s3.max_size}")
    private int maxSize;

    @PostMapping("/{userId}/profilePic")
    @Operation(summary = "Upload new avatar user")
    public UserProfilePicDto uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File weight is more than 5 MB");
        }
        return profilePicService.uploadAvatar(userId, file);
    }

    @GetMapping("/{userId}/profilePic")
    @Operation(summary = "Download avatar user")
    public ResponseEntity<byte[]> downloadAvatarLarge(@PathVariable Long userId) {
        return profilePicService.downloadAvatarLarge(userId);
    }

    @GetMapping("/{userId}/profilePicSmall")
    @Operation(summary = "Download avatar user")
    public ResponseEntity<byte[]> downloadAvatarSmall(@PathVariable Long userId) {
        return profilePicService.downloadAvatarSmall(userId);
    }

    @DeleteMapping("/{userId}/profilePic")
    @Operation(summary = "Delete avatar user")
    public void deleteAvatar(@PathVariable Long userId) {
        profilePicService.deleteAvatar(userId);
    }
}
