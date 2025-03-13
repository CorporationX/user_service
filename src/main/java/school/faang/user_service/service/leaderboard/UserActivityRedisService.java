package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
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

    public void recordUserAction(UserActivity userActivity, UserActivityRequestDto userDto) {
        userActivity.setLastUpdated(LocalDateTime.now());
        String userIdStr = String.valueOf(userDto.userId());
        hashOps.put(USER_HASH_PREFIX + userIdStr, ID_HASH_KEY, String.valueOf(userDto.id()));
        hashOps.put(USER_HASH_PREFIX + userIdStr, USERNAME_HASH_KEY, userDto.username());
        hashOps.put(USER_HASH_PREFIX + userIdStr, COUNTRY_HASH_KEY, userDto.country());
        hashOps.put(USER_HASH_PREFIX + userIdStr, RATING_HASH_KEY, String.valueOf(userActivity.getRating()));
        hashOps.put(USER_HASH_PREFIX + userIdStr, LAST_UPDATED_HASH_KEY, String.valueOf(userActivity.getLastUpdated()));
        zSetOps.add(LEADERBOARD_KEY, userIdStr, userActivity.getRating());

        Long size = zSetOps.size(LEADERBOARD_KEY);
        if (size != null && size > maxCachedLeaderboardSize) {
            zSetOps.removeRange(LEADERBOARD_KEY, 0, size - maxCachedLeaderboardSize - 1);
        }
    }

    public List<UserActivityResponseDto> getTopActiveUsers(int topN) {
        Set<String> topUserIds = zSetOps.reverseRange(LEADERBOARD_KEY, 0, topN - 1);
        return getUserActivities(topUserIds);
    }

    public List<UserActivityResponseDto> getTopActiveUsers(int start, int end) {
        Set<String> topUserIds = zSetOps.reverseRange(LEADERBOARD_KEY, start - 1, end - 1);
        return getUserActivities(topUserIds);
    }

    private List<UserActivityResponseDto> getUserActivities(Set<String> topUserIds) {
        List<UserActivityResponseDto> result = new ArrayList<>();
        if (topUserIds != null) {
            for (String userIdStr : topUserIds) {
                String idStr = hashOps.get(USER_HASH_PREFIX + userIdStr, ID_HASH_KEY);
                String username = hashOps.get(USER_HASH_PREFIX + userIdStr, USERNAME_HASH_KEY);
                String country = hashOps.get(USER_HASH_PREFIX + userIdStr, COUNTRY_HASH_KEY);
                String lastUpdatedStr = hashOps.get(USER_HASH_PREFIX + userIdStr, LAST_UPDATED_HASH_KEY);
                Double scoreDouble = zSetOps.score(LEADERBOARD_KEY, userIdStr);

                LocalDateTime lastUpdated = (lastUpdatedStr != null) ? LocalDateTime.parse(lastUpdatedStr) : null;
                Long id = (idStr != null && !idStr.equals("null")) ? Long.valueOf(idStr) : null;
                Long userId = Long.valueOf(userIdStr);
                Long score = (scoreDouble != null) ? scoreDouble.longValue() : 0L;

                UserActivityResponseDto responseDto = new UserActivityResponseDto(
                        id, userId, username, country, score);
                result.add(responseDto);
            }
        }
        return result;
    }
}
