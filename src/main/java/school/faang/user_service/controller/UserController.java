package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

@Tag(name = "Управление пользователями")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "Регистрация пользователя",
            description = "Позволяет зарегистрировать нового пользователя и сгенерировать аватара")
    @PostMapping("/users")
    public UserDto createUser(@RequestBody @Valid UserDto user) {
        log.info("Accepted request to create new user " + user);
        return userService.createUser(user);
    }
}