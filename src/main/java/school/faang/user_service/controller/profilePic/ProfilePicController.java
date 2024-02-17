package school.faang.user_service.controller.profilePic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.profilePic.ProfilePicService;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class ProfilePicController {
    private final ProfilePicService profilePicService;
    @Value("${services.s3.maxSizeMb}")
    private int maxSize;

    @PutMapping("/pic/{userId}")
    public UserProfilePic userAddProfilePic(@PathVariable long userId,
                                            @RequestPart MultipartFile file) {
        if (file.getSize() > maxSize) {
            throw new DataValidationException("Размер файла не должен превышать 5 Мб");
        }
        return profilePicService.userAddProfilePic(userId, file);
    }

    @GetMapping("/pic")
    public UserProfilePic userGetProfilePic(@RequestBody UserProfilePic userProfilePic) {
        return profilePicService.userGetProfilePic(userProfilePic);
    }

    @DeleteMapping("/pic")
    public void userDeleteProfilePic(@RequestBody UserProfilePic userProfilePic) {
        profilePicService.userDeleteProfilePic(userProfilePic);
    }
}
