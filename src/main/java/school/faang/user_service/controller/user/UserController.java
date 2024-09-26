package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.ConfidentialUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/premium")
    public List<UserDto> getPremiumUser(@RequestBody @Valid UserFilterDto filterDto) {
        List<User> premiumUsers = userService.findPremiumUser(filterDto);
        return userMapper.toDto(premiumUsers);
    }
    
    @PostMapping
    public UserDto registerUser(@RequestBody @Valid ConfidentialUserDto dto) {
        User toRegister = userMapper.toEntity(dto);
        User registered = userService.registerNewUser(toRegister);
        return userMapper.toDto(registered);
    }
}
