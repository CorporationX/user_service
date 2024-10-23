package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@Validated
@RequestMapping("/users")
@Data
@Slf4j
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

    @PutMapping("/updateTelegramUserId")
    public void updateTelegramUserId(
            @RequestParam String telegramUserName,
            @RequestParam String telegramUserId) {
        userService.updateTelegramUserId(telegramUserName, telegramUserId);
    }

    @Operation(summary = "Get user profile", description = "Retrieve the profile of a user by user ID.")
    @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true,
            description = "ID of the user making the request", schema = @Schema(type = "string"))
    @GetMapping("/{user_id}/profile")
    public UserDto getUserProfile(
            @Parameter(description = "User ID to fetch", required = true)
            @PathVariable("user_id") @Positive(message = "ID cannot be less than 1") long userId) {

        Long viewerId = null;
        try {
            viewerId = userContext.getUserId();
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "x-user-id header is missing or invalid", exception);
        }

        UserDto userDto = userService.getUser(userId);
        userService.publishProfileViewEvent(viewerId, userId);

        return userDto;
    }
}
