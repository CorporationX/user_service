package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private void getPremiumUsers(UserFilterDto userFilterDto) {
        userService.getPremiumUsers(userFilterDto);
    }
}
