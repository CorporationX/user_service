package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserCreateDto;
import school.faang.user_service.service.UserProfilePicService;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserProfilePicService userProfilePicService;
    @PostMapping
    public UserCreateDto createUser(@RequestBody UserCreateDto userDto) {
        userProfilePicService.putDefaultPicWhileCreating(userDto);
        return userService.createUser(userDto);
    }
}