package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping("/premium")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getPremiumUser(@RequestBody UserFilterDto filterDto) {
        List<User> premiumUsers = userService.findPremiumUser(filterDto);
        return mapper.toDtoList(premiumUsers);
    }
}
