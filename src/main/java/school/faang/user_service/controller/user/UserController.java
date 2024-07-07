package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.image.ImageProcessor;
import school.faang.user_service.service.user.parse.DataFromFileService;

import java.io.IOException;
import java.util.List;

import static school.faang.user_service.exception.message.ExceptionMessage.*;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private static final double MAX_AVATAR_SIZE = 5_242_880L;

    private final UserService userService;

    private final ImageProcessor imageProcessor;

    private final DataFromFileService dataFromFileService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    UserDto getUser(@Valid @Min(1) @PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping ("/ids")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) { return userService.getUsersByIds(ids); }

    @PostMapping("/{userId}/avatar")
    public UserDto uploadUserAvatar(@PathVariable Long userId, MultipartFile file) {
        if (file.getSize() > MAX_AVATAR_SIZE) {
            log.error(AVATAR_FILE_SIZE_EXCEPTION.getMessage() + "(userId = " + userId + ")");
            throw new DataValidationException(AVATAR_FILE_SIZE_EXCEPTION.getMessage());
        }

        log.info("Uploading avatar for user with id = " + userId);
        return userService.uploadUserAvatar(userId, imageProcessor.getBufferedImage(file));
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<byte[]> downloadUserAvatar(@PathVariable Long userId) {
        log.info("Downloading avatar for user with id = " + userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(userService.downloadUserAvatar(userId), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/avatar")
    public void deleteUserAvatar(@PathVariable Long userId) {
        log.info("Deleting avatar for user with id = " + userId);

        userService.deleteUserAvatar(userId);
    }

    @PostMapping("/bulk-data")
    List<UserDto> uploadData(@NotNull(message = "The file must not be null") @RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.error(NO_FILE_IN_REQUEST.getMessage());
            throw new DataValidationException(NO_FILE_IN_REQUEST.getMessage());
        }
        try {
            return dataFromFileService.saveUsersFromFile(multipartFile.getInputStream());
        } catch (IOException e) {
            log.error(INPUT_OUTPUT_EXCEPTION.getMessage());
            throw new DataValidationException(INPUT_OUTPUT_EXCEPTION.getMessage());
        }
    }

    @GetMapping("/{userId}/exists")
    public boolean existsById(@PathVariable long userId) {
        return userService.existsById(userId);
    }
}
