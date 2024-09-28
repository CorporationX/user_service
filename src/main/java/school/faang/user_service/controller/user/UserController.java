package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserDeactivationService;
import school.faang.user_service.service.user.UserProfilePicService;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDeactivationService userDeactivationService;
    private final UserProfilePicService userProfilePicService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PatchMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public void deactivateUserAccount(@PathVariable Long userId) {
        userDeactivationService.deactivateAccount(userId);
    }

    @PutMapping("/{userId}/avatar")
    @ResponseStatus(HttpStatus.OK)
    public void uploadUserAvatar(@PathVariable Long userId, @RequestBody MultipartFile file) {
        userProfilePicService.uploadUserAvatar(userId, file);
    }

    @GetMapping(value = "/{userId}/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadUserAvatar(@PathVariable Long userId) {
        byte[] img;
        try {
            img = userProfilePicService.downloadUserAvatar(userId).readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to download picture", ex.getCause());
        }

        return new ResponseEntity<>(img, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserAvatar(@PathVariable Long userId) {
        userProfilePicService.deleteUserAvatar(userId);
    }
}
