package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserDto deactivateUser(Long userId) {
        return userService.deactivateUser(userId);
    }

    @GetMapping("/user/{id}")
    public UserDto findUserAndReturnDto(@PathVariable Long id) {
        return userService.findUserAndReturnDto(id);
    }
}