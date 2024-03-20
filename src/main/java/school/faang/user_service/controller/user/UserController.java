package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    @PostMapping
    public UserRegistrationDto createUser(@RequestBody UserRegistrationDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}/pic")
    public UserProfilePic getUserProfilePic(@PathVariable long userId) {
        return userService.getUserPicUrlById(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUserDtoById(userId);
    }

    @GetMapping("/exists/{id}")
    public boolean existsUserById(@PathVariable long id) {
        return userService.isOwnerExistById(id);
    }

    @PostMapping("/{userId}/deactivate/")
    public void deactivateUserById(@PathVariable long userId) {
        userService.deactivationUserById(userId);
    }

    @GetMapping("/premium/filter")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("/students")
    public void registerStudents(@RequestParam("file") MultipartFile csvFile) {
        userService.saveStudents(csvFile);
    }

    @PostMapping("/users")
    public List<UserDto> searchUsers(@RequestBody UserFilterDto filter) {
        long actorId = userContext.getUserId();

        return userService.getUsers(filter, actorId);
    }
}