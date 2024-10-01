package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Positive Long userId) {

        // TODO:: Do normal method that communicates with database because this one is such a shit xd

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("Eblan228");
        return userDto;
    }

    @Operation(
            summary = "Получить список премиум-пользователей",
            description = "Возвращает список премиум-пользователей, отфильтрованных на основе предоставленных критериев.",
            parameters = {
                    @Parameter(
                            name = "filter",
                            description = "Фильтры для поиска пользователей"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список премиум-пользователей",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@ParameterObject @NotNull UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    @PutMapping("/{id}")
    public void deactivateUserProfile(@PathVariable @Positive long id) {
        userService.deactivateUserProfile(id);
    }
}
