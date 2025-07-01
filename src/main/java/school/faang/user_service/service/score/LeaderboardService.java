package school.faang.user_service.service.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.score.UserScore;
import school.faang.user_service.model.score.LeaderboardEntry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private static final String LEADERBOARD_KEY = "Leaderboard";
    private static final int LEADERBOARD_LIMIT = 100;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserScoreService userScoreService;

    public synchronized void init() {
        Long currentSize = redisTemplate.opsForZSet().size(LEADERBOARD_KEY);
        if (currentSize != null && currentSize > 0) {
            log.info("Leaderboard уже инициализирован. Пропуск инициализации (size={})", currentSize);
            return;
        }

        log.info("Инициализация Leaderboard начата");

        userScoreService.getUserScores().stream()
            .sorted(Comparator.comparing(UserScore::getScore).reversed())
            .limit(LEADERBOARD_LIMIT)
            .map(userScore -> new LeaderboardEntry(userScore.getUser().getId(), userScore.getScore()))
            .forEach(entry -> updateLeaderboard(entry.getUserId(), entry.getTotalScore()));

        log.info("Инициализация Leaderboard завершена (записей={})", LEADERBOARD_LIMIT);
    }

    public List<LeaderboardEntry> getTopN() {
        Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(LEADERBOARD_KEY, 0, LEADERBOARD_LIMIT - 1);

        return Optional.ofNullable(tuples)
                .orElse(Set.of())
                .stream()
                .filter(tuple -> tuple.getValue() != null && tuple.getScore() != null)
                .map(tuple -> {
                    long userId = ((Number) tuple.getValue()).longValue();
                    long score = tuple.getScore().longValue();
                    return new LeaderboardEntry(userId, score);
                })
                .toList();
    }

    public void updateLeaderboard(long userId, double absoluteScore) {
        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, userId, absoluteScore);
        redisTemplate.opsForZSet().removeRange(LEADERBOARD_KEY, 0, -LEADERBOARD_LIMIT - 1);
    }
}
