package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@ParameterObject UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    @PutMapping("/{user_id}")
    public String deactivateUserProfile(@PathVariable("user_id") long id) {
        userService.deactivateUserProfile(id);
        return "User deactivated successfully";
    }

    @GetMapping("/{user_id}")
    public UserDto getUser(@PathVariable("user_id") long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam("user_id") List<Long> userIds) {
        return userService.getUsersByIds(userIds);
    }
}
