package school.faang.user_service.controller.ProfilePic;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.ProfilePic.ProfilePicService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pic")
public class ProfilePicController {
    private final ProfilePicService profilePicService;

    @Value("${services.s3.maxSizeBytes}")
    private int maxSizeBytes;

    @PostMapping("/{userId}")
    public UserProfilePic saveProfilePic(@PathVariable long userId, @RequestParam("file") MultipartFile file) {
        if (file.getSize() > maxSizeBytes) {
            throw new DataValidationException("The maximum file size of 5 MB has been exceeded");
        }
        return profilePicService.saveProfilePic(userId, file);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getProfilePic(@PathVariable long userId) {
        byte[] imageBytes = null;
        try{
            imageBytes = profilePicService.getProfilePic(userId).getInputStream().readAllBytes();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

//    @GetMapping("/{userId}")
//    public InputStreamResource getProfilePic(@PathVariable long userId) {
//        return profilePicService.getProfilePic(userId);
//    }

    @DeleteMapping("/{userId}")
    public String deleteProfilePic(@PathVariable long userId) {
        return profilePicService.deleteProfilePic(userId);
    }
}
