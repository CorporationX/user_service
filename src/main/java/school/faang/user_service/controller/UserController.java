package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExternalResourceNotFoundException;
import school.faang.user_service.exception.ExternalServiceError;
import school.faang.user_service.service.UserService;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "User API", description = "Super API to interact with users table")
public class UserController {
    private final UserService userService;

    @PutMapping("/deactivate")
    public UserDto deactivateUser() {
        return userService.deactivateUser();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id", description = "Returns a user DTO")
    public UserDto getUser(@PathVariable long userId) {
        log.info("GET request to /api/v1/users/{}", userId);
        return userService.getUser(userId);
    }

    @PostMapping("/by-ids")
    @Operation(summary = "Get users by ids", description = "Returns a list of user DTOs")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        log.info("POST request to /api/v1/users with body: {}", ids);
        return userService.getUsersByIds(ids);
    }

    @PostMapping()
    @Operation(summary = "Create user")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto userDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
        } catch (DataValidationException ex) {
            return ResponseEntity.badRequest().build();
        } catch (ExternalResourceNotFoundException | ExternalServiceError ex) {
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
