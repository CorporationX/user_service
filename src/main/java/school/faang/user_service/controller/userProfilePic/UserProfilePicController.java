package school.faang.user_service.controller.userProfilePic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfile.UserProfileDto;
import school.faang.user_service.service.userProfilePic.UserProfilePicService;
import school.faang.user_service.validator.userProfilePic.UserProfilePicValidation;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile/image")
public class UserProfilePicController {
    private final UserProfilePicService userProfilePicService;
    private final UserProfilePicValidation userProfilePicValidation;

    @PostMapping("/creation/{userId}")
    public ResponseEntity<UserProfileDto> addImageInProfile(@PathVariable Long userId,
                                                            @RequestParam("file") MultipartFile multipartFile) throws IOException {
        userProfilePicValidation.validMaxSize(multipartFile);
        return ResponseEntity.status(HttpStatus.OK).body(userProfilePicService.addImageInProfile(userId, multipartFile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getImageFromProfile(@PathVariable Long userId) throws IOException {
        byte[] result = userProfilePicService.getImageFromProfile(userId).readAllBytes();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserProfileDto> deleteImageFromProfile(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userProfilePicService.deleteImageFromProfile(userId));
    }
}