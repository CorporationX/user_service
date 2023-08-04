package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfilePic.UserProfilePicDto;
import school.faang.user_service.service.UserProfilePicService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users/avatars/")
public class UserProfilePicController {
    private final UserProfilePicService userProfilePicService;

    @PostMapping("{userId}")
    public UserProfilePicDto upload(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        return userProfilePicService.upload(file);
    }
}
