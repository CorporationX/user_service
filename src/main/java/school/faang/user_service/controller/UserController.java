package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@ParameterObject UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @GetMapping("/regular")
    public List<UserDto> getRegularUsers(@ParameterObject UserFilterDto userFilterDto) {
        return userService.getRegularUsers(userFilterDto);
    }

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable Long id) {
        return userService.existsById(id);
    }
}