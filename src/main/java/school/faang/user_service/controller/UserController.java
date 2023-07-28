package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.request.UserIdsRequest;
import school.faang.user_service.service.UserService;
import school.faang.user_service.util.validator.UserControllerValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserControllerValidator validator;

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserDto getUser(@PathVariable Long userId) {
        validator.validateId(userId);

        return service.getUser(userId);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getUsersByIds(@Valid @RequestBody UserIdsRequest request) {
        return service.getUsersByIds(request.ids());
    }
}
