package school.faang.user_service.repository.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import school.faang.user_service.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class TopUserCacheRepository {
    private static final String REDIS_KEY_TOP_USERS = "topusers";
    private final RedisTemplate<String, UserDto> redisTemplate;

    public boolean save(UserDto userDto, Double score) {
        Boolean addedResult = redisTemplate.opsForZSet().add(REDIS_KEY_TOP_USERS, userDto, score);
        return Boolean.TRUE.equals(addedResult);
    }

    public void saveAll(Map<UserDto, Double> usersRatingScores) {
        usersRatingScores.forEach(this::save);
    }

    public List<UserDto> getTopUsers(int limit) {
        return Objects.requireNonNull(redisTemplate.opsForZSet()
                        .reverseRangeWithScores(REDIS_KEY_TOP_USERS, 0, limit-1))
                .stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();
    }

}
