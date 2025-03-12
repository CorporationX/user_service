package school.faang.user_service.controller.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.leaderboard.UserPopularityService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/top-popular")
public class UserPopularityController {
    private final UserPopularityService userPopularityService;

}
