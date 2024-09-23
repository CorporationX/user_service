package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userMapper.toDto(userRepository
                .findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found")));
    }
}
