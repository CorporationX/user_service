package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.ProfileViewEvent;
import school.faang.user_service.dto.user.UserAvatarSize;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.publisher.mentorshipoffered.ProfileViewEventPublisher;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.utilities.UrlUtils;

import java.time.LocalDateTime;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.USERS)
public class UserController {
    private final UserService userService;
    private final ProfileViewEventPublisher profileViewEventPublisher;

    @GetMapping(UrlUtils.ID)
    public UserDto getUser(@PathVariable("id") @Min(1) Long id, @RequestParam("idRequester") @Min(1) Long idRequester) {
        if (!Objects.equals(id, idRequester)) {
            profileViewEventPublisher.publish(new ProfileViewEvent(idRequester, id, LocalDateTime.now()));
        }
        return userService.getUser(id);
    }

    @PostMapping()
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PutMapping(UrlUtils.ID + UrlUtils.AVATAR)
    public void updateUserAvatar(@PathVariable("id") @Min(1) Long id, @RequestBody MultipartFile avatar) {
        userService.updateUserAvatar(id, avatar);
    }

    @GetMapping(value = UrlUtils.ID + UrlUtils.AVATAR + UrlUtils.SMALL)
    public ResponseEntity<byte[]> getSmallAvatar(@PathVariable("id") @Min(1) Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(userService.getUserAvatar(id, UserAvatarSize.SMALL));
    }

    @GetMapping(value = UrlUtils.ID + UrlUtils.AVATAR + UrlUtils.LARGE)
    public ResponseEntity<byte[]> getLargeAvatar(@PathVariable("id") @Min(1) Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(userService.getUserAvatar(id, UserAvatarSize.LARGE));
    }

    @DeleteMapping(UrlUtils.ID + UrlUtils.AVATAR)
    public void deleteUserAvatar(@PathVariable("id") @Min(1) Long id) {
        userService.deleteUserAvatar(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        try (InputStream is = file.getInputStream()) {
            userService.importUsersFromCSV(is);
            return ResponseEntity.ok("Users successfully imported");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file");
        }
    }
}
