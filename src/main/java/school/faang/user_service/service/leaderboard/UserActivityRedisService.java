package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
import school.faang.user_service.entity.leaderboard.UserActivity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOps;
    private final HashOperations<String, String, String> hashOps;
    private final ReentrantLock lock = new ReentrantLock();

    private static final String LEADERBOARD_KEY = "activityLeaderboard";
    private static final String USER_HASH_PREFIX = "activityUser:";
    private static final String USERNAME_HASH_KEY = "activityUsername:";
    private static final String COUNTRY_HASH_KEY = "activityCountry:";
    private static final String RATING_HASH_KEY = "activityRating:";
    private static final String ID_HASH_KEY = "activityId:";

    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void recordUserAction(UserActivity userActivity, UserActivityRequestDto activityDto) {
        String userIdStr = String.valueOf(activityDto.userId());
        String userKey = USER_HASH_PREFIX + userIdStr;
        hashOps.put(userKey, ID_HASH_KEY, String.valueOf(activityDto.id()));
        hashOps.put(userKey, USERNAME_HASH_KEY, activityDto.username());
        hashOps.put(userKey, COUNTRY_HASH_KEY, activityDto.country());
        hashOps.put(userKey, RATING_HASH_KEY, String.valueOf(userActivity.getRating()));
        zSetOps.add(LEADERBOARD_KEY, userIdStr, userActivity.getRating());

        lock.lock();
        Long size = zSetOps.size(LEADERBOARD_KEY);
        if (size != null && size > maxCachedLeaderboardSize) {
            Set<String> removedUserSet = zSetOps.range(LEADERBOARD_KEY, 0, 0);
            if (removedUserSet != null && !removedUserSet.isEmpty()) {
                String removedUserId = removedUserSet.iterator().next();
                zSetOps.remove(LEADERBOARD_KEY, removedUserId);
                redisTemplate.delete(USER_HASH_PREFIX + removedUserId);
            }
        }
        lock.unlock();
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
        if (topUserIds == null || topUserIds.isEmpty()) {
            return result;
        }

        List<Object> pipelineResults = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            List<Object> results = new ArrayList<>();
            for (String userIdStr : topUserIds) {
                String key = USER_HASH_PREFIX + userIdStr;
                results.add(connection.hashCommands().hGet(
                        key.getBytes(StandardCharsets.UTF_8),
                        ID_HASH_KEY.getBytes(StandardCharsets.UTF_8)
                ));
                results.add(connection.hashCommands().hGet(
                        key.getBytes(StandardCharsets.UTF_8),
                        USERNAME_HASH_KEY.getBytes(StandardCharsets.UTF_8)
                ));
                results.add(connection.hashCommands().hGet(
                        key.getBytes(StandardCharsets.UTF_8),
                        COUNTRY_HASH_KEY.getBytes(StandardCharsets.UTF_8)
                ));
                results.add(connection.zSetCommands().zScore(
                        LEADERBOARD_KEY.getBytes(StandardCharsets.UTF_8),
                        userIdStr.getBytes(StandardCharsets.UTF_8)
                ));
            }
            return null;
        });

        int index = 0;
        for (String userIdStr : topUserIds) {
            String idStr = (String) pipelineResults.get(index++);
            String username = (String) pipelineResults.get(index++);
            String country = (String) pipelineResults.get(index++);
            Double scoreDouble = (Double) pipelineResults.get(index++);

            Long id = (idStr != null && !idStr.equals("null")) ? Long.valueOf(idStr) : null;
            Long userId = Long.valueOf(userIdStr);
            Long score = (scoreDouble != null) ? scoreDouble.longValue() : 0L;

            UserActivityResponseDto responseDto = new UserActivityResponseDto(id, userId, username, country, score);
            result.add(responseDto);
        }
        return result;
    }
}
