package school.faang.user_service.controller.fileUpload;

import net.coobird.thumbnailator.Thumbnails;
import school.faang.user_service.service.S3Service;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/avatar")
public class FileUploadController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final S3Service s3Service;
    private final UserRepository userRepository;

    @Autowired
    public FileUploadController(S3Service s3Service, UserRepository userRepository) {
        this.s3Service = s3Service;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("File size exceeds limit (5 MB)");
        }

        try {
            Path tempFile = Files.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);

            Path largeImage = createResizedImage(tempFile, 1080, "large");
            String largeImageKey = "avatars/large_" + file.getOriginalFilename();
            s3Service.uploadFile(largeImage, largeImageKey);
            Files.delete(largeImage);

            Path smallImage = createResizedImage(tempFile, 170, "small");
            String smallImageKey = "avatars/small_" + file.getOriginalFilename();
            s3Service.uploadFile(smallImage, smallImageKey);
            Files.delete(smallImage);

            Files.delete(tempFile);

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            UserProfilePic profilePic = new UserProfilePic(largeImageKey, smallImageKey);
            user.setUserProfilePic(profilePic);
            userRepository.save(user);

            return ResponseEntity.ok("Upload successful");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfilePic> getUserProfilePic(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user.getUserProfilePic());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserProfilePic(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserProfilePic profilePic = user.getUserProfilePic();
        if (profilePic != null) {
            s3Service.deleteFile(profilePic.getFileId());
            s3Service.deleteFile(profilePic.getSmallFileId());
            user.setUserProfilePic(null);
            userRepository.save(user);
        }
        return ResponseEntity.ok("Avatar deleted successfully");
    }

    private Path createResizedImage(Path originalFile, int maxSize, String suffix) throws IOException {
        Path resizedFile = Files.createTempFile("temp_resized_" + suffix, originalFile.getFileName().toString());
        Thumbnails.of(originalFile.toFile())
                .size(maxSize, maxSize)
                .toFile(resizedFile.toFile());
        return resizedFile;
    }
}