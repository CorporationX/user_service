package school.faang.user_service.controller.userProfilePic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.userProfilePic.UserProfilePicService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserProfilePicController {
    private final UserProfilePicService userProfilePicService;


    @PostMapping("/{user-id}/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProfileImage(@PathVariable("user-id") Long userId, @RequestBody MultipartFile file) throws IOException {
        userProfilePicService.addProfileImage(userId, file);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfileImage(@PathVariable("user-id") Long userId) {
        userProfilePicService.deleteProfileImage(userId);
    }

    @GetMapping("/{user-id}/profile-image")
    public void getDownloadFile() {

    }

}
