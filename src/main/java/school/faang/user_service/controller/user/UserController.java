package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            throw new IllegalArgumentException("Аргумент метода getPremiumUsers не может быть null");
        }

        return userService.getPremiumUsers(userFilterDto);
    }
}