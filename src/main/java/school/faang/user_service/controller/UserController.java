package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserAvatarDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/deactivation/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deactivate(@PathVariable long id) {
        userService.deactivate(id);
    }

    @PostMapping("/{userId}/avatar")
    public UserAvatarDto getAvatar(@RequestBody UserDto userDto, @PathVariable Long userId) {
        return userService.getRandomAvatar(userDto, userId);
    }
}
