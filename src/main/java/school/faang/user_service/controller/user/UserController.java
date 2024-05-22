package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.image.ImageProcessor;

import static school.faang.user_service.exception.ExceptionMessage.FILE_SIZE_EXCEPTION;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final double MAX_AVATAR_SIZE = 5_242_880L;
    private final UserService userService;
    private final ImageProcessor imageProcessor;

    @PutMapping("/{userId}/pic")
    public UserDto uploadUserPic(@PathVariable Long userId, MultipartFile file) {
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new DataValidationException(FILE_SIZE_EXCEPTION.getMessage());
        }

        return userService.uploadUserPic(userId, imageProcessor.getBufferedImage(file));
    }

    @GetMapping("/{userId}/pic")
    public ResponseEntity<byte[]> downloadUserPic(@PathVariable Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(userService.downloadUserPic(userId), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/pic")
    public void deleteUserPic(@PathVariable Long userId) {
        userService.deleteUserPic(userId);
    }
}
