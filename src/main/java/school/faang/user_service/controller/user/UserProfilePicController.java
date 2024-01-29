package school.faang.user_service.controller.user;

import jakarta.servlet.annotation.MultipartConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@MultipartConfig(maxFileSize = 1024 * 1024 * 5)
public class UserProfilePicController {

    @PostMapping("/upload-profile-pic")
    public ResponseEntity<String> uploadProfilePic(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok("Users profile picture uploaded successfully");
    }
}