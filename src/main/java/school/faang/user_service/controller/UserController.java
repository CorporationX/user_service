package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ResponseBody
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @ResponseBody
    @GetMapping("/exists/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkUserExistence(@PathVariable Long userId) {
        return userService.checkUserExistence(userId);
    }

    @ResponseBody
    @GetMapping("/{userId}/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUserFollowers(@PathVariable Long userId) {
        return userService.getUserFollowers(userId);
    }

    @ResponseBody
    @PostMapping("/exists/followers")
    @ResponseStatus(HttpStatus.OK)
    public boolean doesFollowersExist(@RequestBody List<Long> followerIds) {
        return userService.checkAllFollowersExist(followerIds);
    }
}