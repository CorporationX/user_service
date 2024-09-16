package school.faang.user_service.controller.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Data
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/deactivate")
    public UserDto deactivateUser(@RequestBody UserDto userDto) {
        return userService.deactivateUser(userDto);
    }

    @GetMapping(value = "/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }
}