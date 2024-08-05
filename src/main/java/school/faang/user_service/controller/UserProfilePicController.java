package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.service.UserProfilePicService;

import java.io.IOException;

@RestController
@RequestMapping("/profile/pic")
@RequiredArgsConstructor
public class UserProfilePicController {
    private final UserProfilePicService userProfilePicService;

    @PostMapping("/{id}")
    public UserProfilePicDto saveProfilePic(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return userProfilePicService.saveUserProfilePic(id, file);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getProfilePic(@PathVariable Long id) throws IOException {
        byte[] image = userProfilePicService.getUserProfilePic(id).readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public UserProfilePicDto deleteProfilePic(@PathVariable Long id) {
        return userProfilePicService.deleteUserProfilePic(id);
    }
}
