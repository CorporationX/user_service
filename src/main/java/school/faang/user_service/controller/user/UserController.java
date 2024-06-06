package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserFilterDtoValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserFilterDtoValidator userFilterDtoValidator;

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        userFilterDtoValidator.checkIsNull(userFilterDto);
        return userService.getPremiumUsers(userFilterDto);
    }

    @PostMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}