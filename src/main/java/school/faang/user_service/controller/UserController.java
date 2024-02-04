package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @PostMapping("/users")
    public UserDto createUser(@RequestBody UserDto user) {
        userValidator.validateCreateUser(user);
        log.info("Accepted request to create new user " + user);
        return userService.createUser(user);
    }
}