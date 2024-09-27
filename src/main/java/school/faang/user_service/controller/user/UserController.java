package school.faang.user_service.controller.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.image.ImageProcessor;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {
    private static final double MAX_AVATAR_SIZE = 5_242_880L;
    private final UserService userService;
    private final ImageProcessor imageProcessor;

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable @Positive long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody @NotEmpty List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/{userId}/avatar")
    public UserDto uploadUserAvatar(@PathVariable long userId, MultipartFile file) {
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new IllegalArgumentException("Avatar size is too large");
        }
        return userService.uploadUserAvatar(userId, imageProcessor.getBufferedImage(file));
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable long userId, @RequestParam String size) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        byte[] avatarBytes = userService.downloadUserAvatar(userId, size);
        return new ResponseEntity<>(avatarBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/avatar")
    public void deleteUserAvatar(@PathVariable long userId) {
        userService.deleteUserAvatar(userId);
    }
}
