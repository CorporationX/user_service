package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private static final String USER_ID_PATH = "/{userId}";

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userMapper.toDto(userService.getUsersByIds(ids));
    }

    @GetMapping(USER_ID_PATH)
    public UserDto getUser(@PathVariable long userId) {
        return userMapper.toDto(userService.getUserById(userId));
    }
}
