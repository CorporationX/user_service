package school.faang.user_service.controller.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    @GetMapping(value = "/filtered")
    public List<UserDto> getFilteredUsers(@RequestBody UserFilterDto filter) {
        long userId = userContext.getUserId();
        return userService.getFilteredUsers(filter, userId);
    }
}
