package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@Tag(name = "Управление пользователями")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Деактивировать пользователя по идентификатору")
    @PostMapping("/users/deactivate/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }

    @PostMapping("/users/create")
    public void createUser(@RequestBody UserDto userDto) throws IOException {
        userService.createUser(userDto);
    }

    @Operation(summary = "Найти пользователя по идентификатору")
    @GetMapping("/users/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
