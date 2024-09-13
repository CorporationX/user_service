package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }
}
