package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.util.validator.UserControllerValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final UserControllerValidator validator;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserDto getUser(@PathVariable Long userId) {
        validator.validateId(userId);
        return service.getUser(userId);
    }

    @PostMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return service.getUsersByIds(ids);
    }
}