package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import school.faang.user_service.dto.UserAvatarDto;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.service.user.AvatarService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    @Value("${services.s3.max-file-size}")
    private long maxFileSize;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        MediaType.IMAGE_JPEG_VALUE,
        MediaType.IMAGE_PNG_VALUE
    );

    @GetMapping
    public UserAvatarDto getAvatar(@PathVariable Long userId) {
        return avatarService.getAvatar(userId);
    }

    @PostMapping
    public ResponseEntity<String> addAvatar(@PathVariable Long userId,
                                               @RequestParam("file") MultipartFile file) {
        validateAvatar(file);
        avatarService.addAvatar(userId, file);
        return ResponseEntity.ok("Avatar added successfully.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAvatar(@PathVariable Long userId) {
        avatarService.deleteAvatar(userId);
        return ResponseEntity.ok("Avatar deleted successfully.");
    }

    public void validateAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException("No file provided or file is empty.");
        }
        if (file.getSize() > maxFileSize) {
            throw new FileException("File size exceeds the maximum limit of " + maxFileSize + " bytes.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new FileException("Unsupported file type. Allowed types are: JPEG, PNG.");
        }
    }
}
