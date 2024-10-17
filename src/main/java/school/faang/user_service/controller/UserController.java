package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.service.UserLifeCycleService;
import school.faang.user_service.service.UserProfilePicService;
import school.faang.user_service.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserContext userContext;
    private final UserService userService;
    private final UserLifeCycleService userLifeCycleService;
    private final UserProfilePicService userProfilePicService;

    @PutMapping("/deactivate")
    public void deactivateUser() {
        long id = userContext.getUserId();
        userLifeCycleService.deactivateUser(id);
    }

    @PostMapping("/registration")
    public void registrationUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        log.info("Register user: {}", userRegistrationDto);
        userLifeCycleService.registrationUser(userRegistrationDto);
        log.info("User registration successful");
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/{userId}/locale/contact-preference")
    public UserDto getUserWithLocaleAndContactPreference(@PathVariable @Positive Long userId) {
        return userService.getUserWithLocaleAndContactPreference(userId);
    }

    @PostMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/{userId}/avatar")
    public void uploadAvatar(@RequestParam("file") MultipartFile file) {
        userProfilePicService.uploadAvatar(file);
    }
}
