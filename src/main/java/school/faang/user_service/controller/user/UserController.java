package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.service.user.UserDeactivationService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDeactivationService userDeactivationService;

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

    @PostMapping("/registration")
    public UserDto createUserAccount(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        return userService.registerUser(userRegistrationDto);
    }

    @PostMapping("/parsing")
    public List<UserDto> parseUserCsvFile(@RequestBody MultipartFile multipartFile) {
        return userService.saveUsersFromCsvFile(multipartFile);
    }
}
