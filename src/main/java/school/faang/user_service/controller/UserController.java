package school.faang.user_service.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
@Data
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    @Value("${services.s3.max-image-size-mb}")
    private long maxImageSizeMb;

    @PutMapping("/deactivate")
    public UserDto deactivateUser(@RequestBody UserDto userDto) {
        return userService.deactivateUser(userDto);
    }

    @PostMapping(value = "/filtered")
    public List<UserDto> getFilteredUsers(@RequestBody UserFilterDto filter) {
        long userId = userContext.getUserId();
        return userService.getFilteredUsers(filter, userId);
    }

    @GetMapping(value = "/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + userId);
        }
        return userService.getUser(userId);
    }

    @PostMapping()
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/avatar")
    public void saveAvatar(@RequestParam MultipartFile file) {
        long fileSize = file.getSize();
        long maxSizeBytes = maxImageSizeMb * 1024 * 1024;
        if (fileSize > maxSizeBytes) {
            throw new IllegalArgumentException("File size is too large! Current size is " + fileSize
                    + " bytes, but allowed max is " + maxSizeBytes + " bytes");
        }

        String contentType = file.getContentType();
        if (!MediaType.IMAGE_JPEG_VALUE.equals(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only images jpg are allowed!");
        }

        long userId = userContext.getUserId();
        userService.saveAvatar(userId, file);
    }

    @GetMapping(value = "/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getAvatar() {
        long userId = userContext.getUserId();
        byte[] imageBytes = userService.getAvatar(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(imageBytes);
    }

    @DeleteMapping("/avatar")
    public void deleteAvatar() {
        long userId = userContext.getUserId();
        userService.deleteAvatar(userId);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("The file cannot be empty");
        }

        try (InputStream inputStream = file.getInputStream()) {
            userService.processCsvFile(inputStream);
            return ResponseEntity.ok("The file has been successfully uploaded and processed");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading the file");
        }
    }

    @GetMapping("/maxId")
    public Long getMaxUserId() {
        return userService.getMaxUserId();
    }
}