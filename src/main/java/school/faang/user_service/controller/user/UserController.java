package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        return userService.getPremiumUsers(filters);
    }

    public void deactivateUser(long userId) {
        userService.deactivateUser(userId);
    }
}
