package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.service.UserProfilePicService;

@RestController
@RequiredArgsConstructor
public class UserProfilePicController {
    private final UserProfilePicService userProfilePicService;

    public UserProfilePicDto saveProfilePic(UserProfilePicDto userProfilePicDto) {

    }

    public UserProfilePicDto getProfilePic() {

    }

    public UserProfilePicDto deleteProfilePic() {

    }
}
