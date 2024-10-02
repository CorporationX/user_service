package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

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
    public List<UserDto> getPremiumUsers(@ParameterObject UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    @PutMapping(value = "/{user_id}")
    public ResponseEntity<String> deactivateUserProfile(@PathVariable("user_id") long id) {
        userService.deactivateUserProfile(id);
        return ResponseEntity.ok("{\n\"message\": \"User deactivated successfully\"\n}");
    }

    @GetMapping("/{user_id}")
    public UserDto getUser(@PathVariable("user_id") @Positive(message = "Id can't be least 1") long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam("user_id") List<Long> userIds) {
        return userService.getUsersByIds(userIds);
    }

    @PostMapping("/csvfile")
    public ResponseEntity<String> addUsersFromFile(@RequestParam("file") MultipartFile file) throws IOException {
      userService.addUsersFromFile(file.getInputStream());
      return ResponseEntity.ok("sucessfuly uploaded");
    }
}
