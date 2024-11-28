package school.faang.user_service.controller.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.feed.UserNewsFeedDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/v1/feed")
@RequiredArgsConstructor
public class NewsFeedUserController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserNewsFeedDto> getAllUsersInSystem() {
        return userService.getAllUsers();
    }
}
