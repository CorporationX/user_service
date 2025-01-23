package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.BooleanResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{userId}")
    public void deactivateUser(@RequestParam @NotNull Long userId) {
        userService.deactivateUser(userId);
    }

    @GetMapping("/search/{userId}")
    public BooleanResponse isUserExist(@RequestParam(name = "user_id") Long userId) {
        return new BooleanResponse(userService.isUserExist(userId));
    }

    @GetMapping
    public List<UserDto> getPremiumUsers(@RequestBody(required = false) UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

}
