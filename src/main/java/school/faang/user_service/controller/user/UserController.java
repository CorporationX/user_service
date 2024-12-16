package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;
    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@Valid @PathVariable("userId") long userId) {
        log.info("Received a request to get a user with ID: {}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@Valid @RequestBody List<Long> ids) {
        log.info("Received a request to get users");
        return userService.getUsers(ids);
    }

    @GetMapping("/search")
    public List<Long> searchUsers(@RequestParam Long searchingUserId) {
        log.info("Received a request to get users with searching user ID: {}", searchingUserId);
        return userService.searchUsers(searchingUserId);
    }
}