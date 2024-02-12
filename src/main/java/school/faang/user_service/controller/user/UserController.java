package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }
}
