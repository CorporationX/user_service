package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapping.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/search")
    public List<UserResponseDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        List<User> foundUsers = userService.getPremiumUsers(userFilterDto);
        return userMapper.toDtos(foundUsers);
    }
}
