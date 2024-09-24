package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.userClient.UserClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class ClientController {
    private final UserClientService userClientService;

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userClientService.getUser(userId);
    }

    @PostMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userClientService.getUsersByIds(ids);
    }
}
