package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.DeactivationService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final DeactivationService deactivationService;

    public List<UserDto> getPremiumUsers(UserFilterDto filters) {
        return userService.getPremiumUsers(filters);
    }

    public UserDto deactivateUser(long userId) {
        return deactivationService.deactivateUser(userId);
    }
}
