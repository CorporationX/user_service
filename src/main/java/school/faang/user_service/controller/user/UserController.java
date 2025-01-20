package school.faang.user_service.controller.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable @Positive long userId) {
        User user = userService.getUser(userId);
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping()
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<Long> ids) {
        List<User> userList = userService.getUsersByIds(ids);
        List<UserDto> userDtoList = userMapper.toUserDtoList(userList);
        return ResponseEntity.ok(userDtoList);
    }
}
