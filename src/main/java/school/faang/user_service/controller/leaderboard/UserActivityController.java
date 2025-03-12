package school.faang.user_service.controller.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.leaderboard.UserDto;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.enums.UserAction;
import school.faang.user_service.service.leaderboard.UserActivityRedisService;
import school.faang.user_service.service.leaderboard.UserActivityService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/activity")
public class UserActivityController {
    private final UserActivityService userActivityService;

    @PostMapping("record-action")
    public void recordUserAction(@RequestBody UserDto userDto,
                                 @RequestParam("userAction") UserAction userAction) {
        log.info("{}", userDto);
        log.info("Received request to record {} for user with id {}", userAction, userDto.userId());
        userActivityService.recordUserAction(userDto, userAction.getRating());
        log.info("{} successfully recorded for user with id {}", userAction, userDto.userId());
    }

    @GetMapping("/top-active-users")//использовать pageable | фильтры
    public List<UserActivity> getTopActiveUsers(@RequestParam int topN) {
        log.info("Received request to get top {} active users", topN);
        List<UserActivity> topActiveUsers = userActivityService.getTopActiveUsers(topN);
        log.info("Top {} active users successfully obtained", topN);
        return topActiveUsers;
    }
}
