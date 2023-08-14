package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.service.UserService;
import school.faang.user_service.util.validator.UserControllerValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserControllerValidator validator;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserDto getUser(@PathVariable Long userId) {
        validator.validateId(userId);
        return userService.getUser(userId);
    }

    @PostMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/deactivate/{userId}")
    public ResponseEntity<UserDto> deactivateUser(@RequestHeader(value = "x-user-id") long id, @PathVariable Long userId) {
        if (id != userId) throw new DataValidationException("Only the user can deactivate their own account");
        return ResponseEntity.ok(userService.deactivateUser(userId));
    }
}