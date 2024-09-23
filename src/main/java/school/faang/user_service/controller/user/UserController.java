package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
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

    @PatchMapping("/{id}")
    public void deactivateUserProfile(@PathVariable long id) {
        userService.deactivateUserProfile(id);
    }

    @PostMapping("upload")
    public ResponseEntity<String> addUsersFromFile(@RequestParam("file") MultipartFile file) throws IOException {
      userService.addUsersFromFile(file.getInputStream());
      return ResponseEntity.ok("sucessfuly uploaded");
    }
}
