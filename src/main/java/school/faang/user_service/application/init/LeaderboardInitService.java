package school.faang.user_service.application.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.score.LeaderboardService;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaderboardInitService implements ApplicationRunner {

    private final LeaderboardService leaderboardService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            leaderboardService.init();
        } catch (Exception e) {
            log.error("Ошибка при инициализации Leaderboard", e);
        }
    }
}
