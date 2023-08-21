package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserFilterDto;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mydto.UserDto;
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

    @PostMapping("/deactivate")
    public ResponseEntity<UserDto> deactivateUser(@RequestHeader(value = "x-user-id") long userId) {
        return ResponseEntity.ok(userService.deactivateUser(userId));
    }

    @GetMapping("/users/premium")
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filterDto) {
        return service.getPremiumUsers(filterDto);
    }
}