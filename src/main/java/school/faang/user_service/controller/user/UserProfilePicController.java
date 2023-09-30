package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.userProfilePic.AvatarFromAwsDto;
import school.faang.user_service.dto.userProfilePic.UserProfilePicDto;
import school.faang.user_service.service.UserProfilePicService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${urls.avatar_url}")

public class UserProfilePicController {
    private final UserProfilePicService userProfilePicService;
    private final UserContext userContext;

    @PostMapping
    public UserProfilePicDto upload(@RequestParam("file") MultipartFile file) {
        return userProfilePicService.uploadWithPublishProfilePicEvent(file, userContext.getUserId());
    }

    @GetMapping
    public List<AvatarFromAwsDto> get() {
        return userProfilePicService.getByUserId(userContext.getUserId());
    }

    @GetMapping("/{id}")
    public List<AvatarFromAwsDto> getByUserId(@PathVariable(name = "id") long userId) {
        return userProfilePicService.getByUserId(userId);
    }

    @DeleteMapping
    public UserProfilePicDto delete() {
        return userProfilePicService.deleteByUserId(userContext.getUserId());
    }
}