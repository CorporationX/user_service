package school.faang.user_service.controller.ProfilePic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.UserProfilePicDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.avatar.ProfilePicService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pic")
public class ProfilePicController {
    private final ProfilePicService profilePicService;

    @Value("${services.s3.maxSizeBytes}")
    private int maxSizeBytes;

    @PostMapping("/{userId}")
    public UserProfilePicDto saveProfilePic(@PathVariable long userId, @RequestParam("file") MultipartFile file) {
        if (file.getSize() > maxSizeBytes) {
            throw new DataValidationException("The maximum file size of 5 MB has been exceeded");
        }
        return profilePicService.saveProfilePic(userId, file);
    }

    @GetMapping("/{userId}")
    public InputStreamResource getProfilePic(@PathVariable long userId) {
        return profilePicService.getProfilePic(userId);
    }

    @DeleteMapping("/{userId}")
    public String deleteProfilePic(@PathVariable long userId) {
        return profilePicService.deleteProfilePic(userId);
    }
}
