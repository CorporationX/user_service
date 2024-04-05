package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.UserProfilePicService;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/user/profile-pic")
public class UserProfilePicController {

    @Autowired
    private UserProfilePicService userProfilePicService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> uploadUserProfilePic(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            userProfilePicService.uploadUserProfilePic(userId, file);
            return ResponseEntity.ok("User profile picture uploaded successfully " + file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload user profile picture");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserProfilePic(@PathVariable Long userId) {
        try {
            userProfilePicService.deleteUserProfilePic(userId);
            return ResponseEntity.ok("User profile picture deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user profile picture ");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfilePic(@PathVariable Long userId) {
        try {
            InputStream userProfilePicStream = userProfilePicService.getUserProfilePic(userId);
            // You may want to set appropriate headers and return the InputStream directly as ResponseEntity
            // Here is just an example of returning a success message for demonstration
            return ResponseEntity.ok("User profile picture retrieved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve user profile picture");
        }
    }
}

