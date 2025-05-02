package school.faang.user_service.controller.leaderboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
import school.faang.user_service.service.leaderboard.UserActivityService;

import java.util.List;

@Tag(name = "user_activity_methods")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/activity")
public class UserActivityController {
    private final UserActivityService userActivityService;

    @Operation(
            summary = "Запись действия пользователя",
            description = "Записывает действие пользователя (impact) для отслеживания его активности и влияния на рейтинг популярности."
    )
    @PostMapping("/action")
    public void recordUserAction(@RequestBody UserActivityRequestDto userActivityRequestDto) {
        log.info("{}", userActivityRequestDto);
        log.info("Received request to record {} for user with id {}",
                userActivityRequestDto.userAction(), userActivityRequestDto.userId());
        userActivityService.recordUserAction(userActivityRequestDto, userActivityRequestDto.userAction());
        log.info("{} successfully recorded for user with id {}",
                userActivityRequestDto.userAction(), userActivityRequestDto.userId());
    }

    @Operation(
            summary = "Получение топ-N активных пользователей",
            description = "Возвращает список топ-N пользователей с наибольшей активностью."
    )
    @GetMapping("/top/{topN}")
    public List<UserActivityResponseDto> getTopActiveUsers(@PathVariable("topN") int topN) {
        log.info("Received request to get top {} active users", topN);
        List<UserActivityResponseDto> topActiveUsers = userActivityService.getTopActiveUsers(topN);
        log.info("Top {} active users successfully obtained", topN);
        return topActiveUsers;
    }

    @Operation(
            summary = "Получение активных пользователей в диапазоне",
            description = "Возвращает список активных пользователей в указанном диапазоне (start, end)."
    )
    @GetMapping("/top/range")
    public List<UserActivityResponseDto> getTopActiveUsers(@RequestParam("start") int start,
                                                           @RequestParam("end") int end) {
        log.info("Received request to get top active users in range [{}, {}]", start, end);
        List<UserActivityResponseDto> topActiveUsers = userActivityService.getTopActiveUsers(start, end);
        log.info("Top [{}, {}] active users successfully obtained", start, end);
        return topActiveUsers;
    }
}
