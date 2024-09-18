package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClientController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userRepository.findById(userId).map(userMapper::toDto).orElseThrow();
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDto)
                .toList();
    }
}
