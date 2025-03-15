package school.faang.user_service.controller.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.enums.UserImpactRequestDto;
import school.faang.user_service.service.leaderboard.UserPopularityService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/popularity")
public class UserPopularityController {
    private final UserPopularityService userPopularityService;

    @PostMapping("record-impact")
    public void recordUserAction(@RequestBody UserPopularityRequestDto userDto,
                                 @RequestParam("userImpact") UserImpactRequestDto userImpact) {
        log.info("{}", userDto);
        log.info("Received request to record {} on user with id {}", userImpact, userDto.userId());
        userPopularityService.recordUserImpact(userDto, userImpact.getImpactScore());
        log.info("{} successfully recorded on user with id {}", userImpact, userDto.userId());
    }

    @GetMapping("/top-popular-users")
    public List<UserPopularityResponseDto> getTopActiveUsers(@RequestParam("topN") int topN) {
        log.info("Received request to get top {} popular users", topN);
        List<UserPopularityResponseDto> topPopularUsers = userPopularityService.getTopPopularUsers(topN);
        log.info("Top {} popular users successfully obtained", topN);
        return topPopularUsers;
    }
}
