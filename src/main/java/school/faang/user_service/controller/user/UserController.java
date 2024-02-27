package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserRegistrationDto createUser(@RequestBody UserRegistrationDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}/pic")
    public UserProfilePic getUserProfilePic(@PathVariable long userId) {
        return userService.getUserPicUrlById(userId);
    }

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUserDtoById(userId);
    }

    @GetMapping("/exists/{id}")
    private boolean existsUserById(@PathVariable long id) {
        return userService.isOwnerExistById(id);
    }

    @PostMapping("/{userId}/deactivate/")
    public void deactivateUserById(@PathVariable long userId) {
        userService.deactivationUserById(userId);
    }

    @GetMapping("/premium/filter")
    private List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }
}
