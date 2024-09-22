package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivatedUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public List<UserResponseDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        List<User> foundUsers = userService.getPremiumUsers(userFilterDto);
        return userMapper.toDtos(foundUsers);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable long userId) {
        User user = userService.getUser(userId);
        return userMapper.toDto(user);
    }

    @PostMapping("/all")
    List<UserResponseDto> getUsersByIds(@RequestBody List<Long> ids) {
        System.out.println("hello");
        List<User> users = userService.getUsers(ids);
        return userMapper.toDtos(users);
    }
}
