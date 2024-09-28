package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.ErrorResponse;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Получить список премиум-пользователей",
            description = "Возвращает список премиум-пользователей, отфильтрованных на основе предоставленных критериев.",
            parameters = {
                    @Parameter(
                            name = "filter",
                            description = "Фильтры для поиска пользователей")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Список премиум-пользователей",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибки валидации",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"field\": \"message\"}"))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content)
    })
    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@ParameterObject UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    @Operation(summary = "Деактивация пользователя", description = "Деактивация пользователя по его ID")
    @PutMapping("/{id}")
    public void deactivateUserProfile(@PathVariable long id) {
        userService.deactivateUserProfile(id);
    }

    @Operation(summary = "Получение пользователя", description = "Возвращает пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Получен пользователь",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(
                    responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam("user_id") List<Long> userIds) {
        return userService.getUsersByIds(userIds);
    }
}
