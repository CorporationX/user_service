package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.service.user.UserDeactivationService;
import school.faang.user_service.service.user.UserService;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDeactivationService userDeactivationService;

    @PatchMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public void deactivateUserAccount(@PathVariable Long userId) {
        userDeactivationService.deactivateAccount(userId);
    }

    @PostMapping("/registration")
    public UserDto createUserAccount(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        log.info("Registration user {} started", userRegistrationDto);
        UserDto userDto = userService.registerUser(userRegistrationDto);
        log.info("Registration user {} completed", userDto);
        return userDto;
    }

}
