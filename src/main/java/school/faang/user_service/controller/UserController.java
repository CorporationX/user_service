package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private static final String MESSAGE_USER_EMPTY = "UserDto is empty";
    private final UserService service;

    public UserDto deactivatesUserProfile(UserDto userDto) {
        if (userDto == null) {
            throw new RuntimeException(MESSAGE_USER_EMPTY);
        }
        return service.deactivatesUserProfile(userDto.getId());
    }
}
