package school.faang.user_service.service.score;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.configuration.TestContainersConfig;
import school.faang.user_service.entity.score.UserScore;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.score.LeaderboardEntry;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LeaderboardServiceIT extends TestContainersConfig {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private UserScoreService userScoreService;

    private static final String LEADERBOARD_KEY = "Leaderboard";
    private static final int LEADERBOARD_LIMIT = 100;

    @BeforeEach
    void clearRedis() {
        redisTemplate.delete(LEADERBOARD_KEY);
    }

    @Test
    void testInitLeaderboard() {
        int multiplier = 10;
        List<UserScore> scores = IntStream.range(1, LEADERBOARD_LIMIT * 2)
                .mapToObj(i -> {
                    User user = new User();
                    user.setId((long) i);

                    UserScore userScore = new UserScore();
                    userScore.setUser(user);
                    userScore.setScore(i * multiplier);

                    return userScore;
                })
                .toList();

        long expectedUser = LEADERBOARD_LIMIT * 2 - 1;
        long expectedScore = (LEADERBOARD_LIMIT * 2 - 1) * multiplier;

        Mockito.when(userScoreService.getUserScores()).thenReturn(scores);

        leaderboardService.init();

        List<LeaderboardEntry> top = leaderboardService.getTopN();

        assertThat(top).hasSize(LEADERBOARD_LIMIT);
        assertThat(top.get(0).getTotalScore()).isEqualTo(expectedScore);
        assertThat(top.get(0).getUserId()).isEqualTo(expectedUser);
    }

    @Test
    void testUpdateLeaderboardSingleUser() {
        long userId = 42L;
        long score = 777;

        leaderboardService.updateLeaderboard(userId, score);

        List<LeaderboardEntry> top = leaderboardService.getTopN();
        assertThat(top).contains(new LeaderboardEntry(userId, score));
    }
}
