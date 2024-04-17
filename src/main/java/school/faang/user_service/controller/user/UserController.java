package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("getUser method was called");
        return userService.getUser(userId);
    }

    @PostMapping("/create")
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return userService.create( userDto );
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        log.info("getUsersByIds was called");
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/deactivate/{userId}")
    public void deactivate(@PathVariable long userId) {
        userService.deactivate(userId);
    }
}
