package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;
import java.io.IOException;
import java.util.List;

@Tag(name = "Users", description = "Операции с пользователями")
@Slf4j
@Validated
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public UserDto create(CreateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateString(userDto.password(), "password");
        validateNotNull(userDto.countryId(), "country");
        return userService.create(userDto);
    }

    public UserDto update(long userId, UpdateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateNotNull(userDto.countryId(), "country");
        return userService.update(userId, userDto);
    }

    public UserDto getById(long userId) {
        return userService.getById(userId);
    }

    @Operation(summary = "Получить пользователя по id")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/{userId}")
    public UserDto getUser(
            @PathVariable
            @Min(1) long userId) {
        return userService.getUser(userId);
    }

    @Operation(summary = "Получить список пользователей по списку id")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
    @PostMapping
    public List<UserDto> getUsersByIds(
            @RequestBody
            @NotEmpty List<@Min(1) Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/addStudents")
    public ResponseEntity<List<UserDto>> addStudents(@RequestParam("file") MultipartFile file) throws IOException {
        List<UserDto> students = userService.addStudents(file);
        return ResponseEntity.ok(students);
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}
