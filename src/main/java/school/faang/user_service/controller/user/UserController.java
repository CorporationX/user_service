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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserFilterDtoValidator userFilterDtoValidator;
    private final UserValidator userValidator;

    @PostMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        userFilterDtoValidator.checkIsNull(userFilterDto);

        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("{userid}/pic")
    public UserDto savePic(@PathVariable Long userid, @RequestParam MultipartFile pic) {
        userValidator.checkUserInDB(userid);
        userValidator.checkMaxSizePic(pic);

        return userService.savePic(userid, pic);
    }

    @GetMapping("{userId}/pic")
    public ResponseEntity<byte[]> getPic(@PathVariable Long userId) {
        userValidator.checkUserInDB(userId);

        return userService.getPic(userId);
    }

    @DeleteMapping("{userId}/pic")
    public UserDto deletePic(@PathVariable Long userId) {
        userValidator.checkUserInDB(userId);

        return userService.deletePic(userId);
    }
}