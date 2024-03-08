package school.faang.user_service.controller.profilePic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.profilePic.ProfilePicService;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ProfilePicController {
    private final ProfilePicService profilePicService;

    @Value("${services.s3.maxSizeMb}")
    private int maxSize;

    @PostMapping("/pic/{userId}")
    public UserProfilePic addProfilePic(@PathVariable long userId,
                                            @RequestPart("file") MultipartFile file) {
        if (file.getSize() > maxSize) {
            throw new DataValidationException("Размер файла не должен превышать 5 Мб");
        }
        return profilePicService.addProfilePic(userId, file);
    }

    @GetMapping("/pic/{userId}")
    public ResponseEntity<InputStreamResource> getProfilePic(@PathVariable long userId) {
        return profilePicService.getProfilePic(userId);
    }

    @DeleteMapping("/pic/{userId}")
    public ResponseEntity<String> deleteProfilePic(@PathVariable long userId) {
        return profilePicService.deleteProfilePic(userId);
    }
}
