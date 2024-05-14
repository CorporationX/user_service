package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.UserFilterDtoValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserFilterDtoValidator userFilterDtoValidator;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        userFilterDtoValidator.checkIsNull(userFilterDto);
        return userService.getPremiumUsers(userFilterDto);
    }
}