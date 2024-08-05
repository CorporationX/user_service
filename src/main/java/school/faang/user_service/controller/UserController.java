package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserProfilePicService;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserProfilePicService userProfilePicService;
    @PostMapping
    public UserDto createUser(UserDto userDto) {
        userProfilePicService.putDefaultPicWhileCreating(userDto);
        return userService.createUser(userDto);
    }
}