package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Пользователи сервиса")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userMapper.toDto(userService.getUser(userId));
    }
}
