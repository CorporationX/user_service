package school.faang.user_service.controller.user;

import jakarta.servlet.annotation.MultipartConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.user.UserProfilePicService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfilePicController {

    private final UserProfilePicService userProfilePicService;
    private final UserContext userContext;

    @PostMapping("/upload-profile-pic")
    public ResponseEntity<String> uploadProfilePic(@RequestParam("file") MultipartFile file) {
        userProfilePicService.uploadProfilePic(file, userContext.getUserId());
        return ResponseEntity.ok("Users profile picture uploaded successfully");
    }
}