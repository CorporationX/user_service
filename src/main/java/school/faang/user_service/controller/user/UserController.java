package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserContext userContext;

    @PostMapping
    public UserDto create(@RequestBody CreateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateString(userDto.password(), "password");
        validateNotNull(userDto.countryId(), "country");
        return userService.create(userDto);
    }

    @PutMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UpdateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateNotNull(userDto.countryId(), "country");
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @PostMapping("/avatars")
    public UserDto setUserAvatar(@RequestParam("avatar") MultipartFile avatar) throws IOException {
        return userService.setUserAvatar(userContext.getUserId(), avatar);
    }

    @GetMapping(path = "/avatars", produces = "application/octet-stream")
    public ResponseEntity<byte[]> getUserAvatar() {
        byte[] imageBytes = null;
        try {
            imageBytes = userService.getUserAvatar(userContext.getUserId()).readAllBytes();
        } catch (IOException e) {
            log.info(String.valueOf(e));
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping("/avatars")
    public ResponseEntity<String> deleteUserAvatar() {
        userService.deleteUserAvatar(userContext.getUserId());
        return ResponseEntity.ok("Аватар успешно удален.");

    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isNotBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}
