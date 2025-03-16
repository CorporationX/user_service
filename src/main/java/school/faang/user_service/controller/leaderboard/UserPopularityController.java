package school.faang.user_service.controller.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.service.leaderboard.UserPopularityService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/popularity")
public class UserPopularityController {
    private final UserPopularityService userPopularityService;

    @PostMapping("/impact")
    public void recordUserImpact(@RequestBody UserPopularityRequestDto userPopularityRequestDto) {
        log.info("{}", userPopularityRequestDto);
        log.info("Received request to record {} on user with id {}",
                userPopularityRequestDto.userImpact(), userPopularityRequestDto.userId());
        userPopularityService.recordUserImpact(userPopularityRequestDto, userPopularityRequestDto.userImpact());
        log.info("{} successfully recorded on user with id {}",
                userPopularityRequestDto.userImpact(), userPopularityRequestDto.userId());
    }

    @GetMapping("/top/{topN}")
    public List<UserPopularityResponseDto> getTopPopularUsers(@PathVariable("topN") int topN) {
        log.info("Received request to get top {} popular users", topN);
        List<UserPopularityResponseDto> topPopularUsers = userPopularityService.getTopPopularUsers(topN);
        log.info("Top {} popular users successfully obtained", topN);
        return topPopularUsers;
    }

    @GetMapping("/top/range")
    public List<UserPopularityResponseDto> getTopPopularUsers(@RequestParam("start") int start,
                                                              @RequestParam("end") int end) {
        log.info("Received request to get top popular users in range [{}, {}]", start, end);
        List<UserPopularityResponseDto> topActiveUsers = userPopularityService.getTopPopularUsers(start, end);
        log.info("Top [{}, {}] popular users successfully obtained", start, end);
        return topActiveUsers;
    }
}
