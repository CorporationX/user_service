package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.AvatarStyle;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User registeredUser = userService.registerUser(user);
        UserDto responseDto = userMapper.toDto(registeredUser);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivatedUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);

        return ResponseEntity.noContent().build();
    }
}
