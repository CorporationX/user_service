package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.ReadUserDto;
import school.faang.user_service.dto.user.WriteUserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/byList")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersDtoByIds(ids);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadUserDto createUser(@RequestBody @Valid WriteUserDto writeUserDto) {
        return userService.createUser(writeUserDto);
    }
}