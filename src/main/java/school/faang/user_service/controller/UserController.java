package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserResponseDto;
import school.faang.user_service.service.UserService;

@Slf4j
@RestController
@Tag(name = "User service API", description = "API for User Service")
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        log.debug("getUser userId: {}", userId);
        return userService.getUserDtoById(userId);
    }
}