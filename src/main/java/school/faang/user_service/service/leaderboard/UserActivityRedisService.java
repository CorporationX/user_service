package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserActivityDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.leaderboard.UserActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOps;
    private final HashOperations<String, String, String> hashOps;

    private static final String LEADERBOARD_KEY = "leaderboard";
    private static final String USER_HASH_PREFIX = "user:";
    private static final String USERNAME_HASH_KEY = "username:";
    private static final String COUNTRY_HASH_KEY = "country:";
    private static final String RATING_HASH_KEY = "rating:";
    private static final String LAST_UPDATED_HASH_KEY = "lastUpdated:";
    private static final String ID_HASH_KEY = "id:";

    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void recordUserAction(UserActivity userActivity, UserActivityDto userDto) {
        userActivity.setLastUpdated(LocalDateTime.now());
        String userIdStr = String.valueOf(userDto.userId());
        hashOps.put(USER_HASH_PREFIX + userIdStr, ID_HASH_KEY, String.valueOf(userActivity.getId()));
        hashOps.put(USER_HASH_PREFIX + userIdStr, USERNAME_HASH_KEY, userDto.username());
        hashOps.put(USER_HASH_PREFIX + userIdStr, COUNTRY_HASH_KEY, userDto.country());
        hashOps.put(USER_HASH_PREFIX + userIdStr, RATING_HASH_KEY, String.valueOf(userActivity.getRating()));
        hashOps.put(USER_HASH_PREFIX + userIdStr, LAST_UPDATED_HASH_KEY, String.valueOf(userActivity.getLastUpdated()));

        Long size = zSetOps.size(LEADERBOARD_KEY);
        if (size != null && size > maxCachedLeaderboardSize) {
            zSetOps.removeRange(LEADERBOARD_KEY, 0, size - maxCachedLeaderboardSize - 1);
        }

        log.info("Updated rating in redis cache for user with id {} is {}",
                userDto.userId(), zSetOps.score(LEADERBOARD_KEY, userIdStr));
    }

    public List<UserActivityDto> getTopActiveUsers(int topN) {
        Set<String> topUserIds = zSetOps.reverseRange(LEADERBOARD_KEY, 0, topN - 1);
        System.out.println(topUserIds);
        return getUserActivities(topUserIds);
    }

    public List<UserActivityDto> getTopActiveUsers(int start, int end) {
        Set<String> topUserIds = zSetOps.reverseRange(LEADERBOARD_KEY, start - 1, end - 1);
        return getUserActivities(topUserIds);
    }

    private List<UserActivityDto> getUserActivities(Set<String> topUserIds) {
        List<UserActivityDto> result = new ArrayList<>();
        if (topUserIds != null) {
            for (String userIdStr : topUserIds) {
                Double score = zSetOps.score(LEADERBOARD_KEY, userIdStr);
                String idStr = hashOps.get(USER_HASH_PREFIX + userIdStr, ID_HASH_KEY);
                String username = hashOps.get(USER_HASH_PREFIX + userIdStr, USERNAME_HASH_KEY);
                String country = hashOps.get(USER_HASH_PREFIX + userIdStr, COUNTRY_HASH_KEY);
                String lastUpdatedStr = hashOps.get(USER_HASH_PREFIX + userIdStr, LAST_UPDATED_HASH_KEY);
                LocalDateTime lastUpdated = lastUpdatedStr != null ? LocalDateTime.parse(lastUpdatedStr) : null;
                Long id = (idStr != null && !idStr.equals("null")) ? Long.valueOf(idStr) : null;

                User user;
            }
        }
        return result;
    }
}
