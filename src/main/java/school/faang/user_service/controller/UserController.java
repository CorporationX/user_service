package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

@Tag(name = "Управление пользователями")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "Регистрация пользователя",
            description = "Позволяет зарегистрировать нового пользователя и сгенерировать аватара")
    @PostMapping()
    public UserDto createUser(@RequestBody @Valid UserCreateDto user) {
        log.info("Accepted request to create new user " + user);
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @GetMapping("/exists/{userId}")
    public boolean isUserExists(@PathVariable long userId) {
        return userService.isUserExists(userId);
    }
}