package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.service.UserLifeCycleService;
import school.faang.user_service.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("/api/v1/users")
public class UserController {

    private final UserContext userContext;
    private final UserService userService;
    private final UserLifeCycleService userLifeCycleService;

    @PutMapping("/deactivate")
    public void deactivateUser() {
        long id = userContext.getUserId();
        userLifeCycleService.deactivateUser(id);
    }
    @PutMapping("/deactivate/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userLifeCycleService.deactivateUser(id);
    }
    @PostMapping("/registration")
    public void registrationUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        log.info("Register user: {}", userRegistrationDto);
        userLifeCycleService.registrationUser(userRegistrationDto);
        log.info("User registration successful");
    }
    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
