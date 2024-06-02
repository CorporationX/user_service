package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserFilterDtoValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserFilterDtoValidator userFilterDtoValidator;
    private final UserValidator userValidator;

    @PostMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        userFilterDtoValidator.checkIsNull(userFilterDto);

        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("{userid}/picture")
    public UserDto uploadProfilePicture(@PathVariable Long userid, @RequestParam MultipartFile picture) {
        userValidator.checkUserInDB(userid);
        userValidator.checkMaxSizePic(picture);

        return userService.uploadProfilePicture(userid, picture);
    }

    @GetMapping("{userId}/picture")
    public ResponseEntity<byte[]> downloadProfilePicture(@PathVariable Long userId) {
        userValidator.checkUserInDB(userId);

        return userService.downloadProfilePicture(userId);
    }

    @DeleteMapping("{userId}/picture")
    public UserDto deleteProfilePicture(@PathVariable Long userId) {
        userValidator.checkUserInDB(userId);

        return userService.deleteProfilePicture(userId);
    }
}