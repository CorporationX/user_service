package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.UserMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final UserMapper userMapper;
    private static final String USER_ID_PATH = "/{userId}";

    @GetMapping(USER_ID_PATH)
    public UserDto getUser(@PathVariable long userId) {
        return userMapper.toDto(userRepositoryAdapter.getUserById(userId));
    }
}
