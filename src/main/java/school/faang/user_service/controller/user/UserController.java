package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")

@Tag(name = "Пользователи", description = "Взаимодействие с пользователями")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Создание пользователя",
            description = "Позволяет создать пользователя"
    )
    @PostMapping("/user/create")
    public UserDto create(@RequestBody @Valid CreateUserDto userDto) {
        return userService.create(userDto);
    }

    @Operation(
            summary = "Поиск пользователя по идентификатору",
            description = "Позволяет получить пользователя по его идентификатору"
    )
    @PatchMapping("/user/{userId}/update")
    public UserDto update(long userId, @RequestBody @Valid UpdateUserDto userDto) {
        return userService.update(userId, userDto);
    }

    public UserDto getById(long userId) {
        return userService.getById(userId);
    }

    @Operation(
            summary = "Поиск пользователя по идентификатору",
            description = "Позволяет получить пользователя по его идентификатору"
    )
    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @Operation(
            summary = "Поиск пользователей по идентификаторам",
            description = "Позволяет получить список пользователей по списку идентификаторов"
    )
    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}
