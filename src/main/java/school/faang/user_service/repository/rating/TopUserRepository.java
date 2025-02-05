package school.faang.user_service.repository.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class TopUserRepository {
    private static final String REDIS_KEY_TOP_USERS = "topusers";
    private final RedisTemplate<String, Long> redisTemplate;

    public boolean save(Long userId, Double score) {
        Boolean addedResult = redisTemplate.opsForZSet().add(REDIS_KEY_TOP_USERS, userId, score);
        return Boolean.TRUE.equals(addedResult);
    }

    public Map<Long, Double> getTopUsersWithScores() {
        try {
            return Objects.requireNonNull(redisTemplate.opsForZSet()
                            .reverseRangeWithScores(REDIS_KEY_TOP_USERS, 0, 100))
                    .stream()
                    .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore));
        } catch (NullPointerException e) {
            return Map.of();
        }
    }

    public double getTopUserScore(Long userId) {
        Double score = redisTemplate.opsForZSet().score(REDIS_KEY_TOP_USERS, userId);
        if (score == null) {
            return 0.0;
        }
        return score;
    }

}
