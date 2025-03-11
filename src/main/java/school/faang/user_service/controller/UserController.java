package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.BooleanResponse;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{userId}")
    @Operation(summary = "Деактивировать пользователя", description = "Отключает пользователя по его ID")
    public void deactivateUser(
            @Parameter(description = "ID пользователя для деактивировации", example = "1", required = true)
            @RequestParam @NotNull Long userId) {
        userService.deactivateUser(userId);
    }

    @GetMapping("/is-user-exist/{userId}")
    @Operation(summary = "Проверить существование пользователя", description = "Возвращает true, если пользователь существует")
    public BooleanResponse isUserExist(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @RequestParam(name = "user_id") Long userId) {
        return new BooleanResponse(userService.isUserExist(userId));
    }

    @GetMapping("/premium")
    @Operation(summary = "Получить премиум-пользователей", description = "Возвращает список премиум-пользователей с возможностью фильтрации")
    public List<UserDto> getPremiumUsers(
            @Parameter(description = "Фильтр для поиска премиум-пользователей")
            @RequestBody(required = false) UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать пользователя", description = "Создаёт нового пользователя на основе переданных данных")
    public UserDto createUser(
            @Parameter(description = "Данные для создания пользователя", required = true)
            @Valid @ModelAttribute UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получить пользователя по ID")
    public UserDto getUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    @Operation(summary = "Получить пользователей по списку ID", description = "Возвращает список пользователей по их ID")
    public List<UserDto> getUsersByIds(
            @Parameter(description = "Список ID пользователей")
            @RequestParam List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}