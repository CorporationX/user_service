package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.entity.leaderboard.UserPopularity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPopularityRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOps;
    private final HashOperations<String, String, String> hashOps;
    private final ReentrantLock lock = new ReentrantLock();

    private static final String LEADERBOARD_KEY = "popularityLeaderboard";
    private static final String USER_HASH_PREFIX = "popularityUser:";
    private static final String USERNAME_HASH_KEY = "popularityUsername:";
    private static final String COUNTRY_HASH_KEY = "popularityCountry:";
    private static final String RATING_HASH_KEY = "popularityImpact:";
    private static final String ID_HASH_KEY = "popularityId:";
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void recordUserImpact(UserPopularity userImpact, UserPopularityRequestDto popularityDto) {
        String userIdStr = String.valueOf(popularityDto.userId());
        String userKey = USER_HASH_PREFIX + userIdStr;
        hashOps.put(userKey, ID_HASH_KEY, String.valueOf(popularityDto.id()));
        hashOps.put(userKey, USERNAME_HASH_KEY, popularityDto.username());
        hashOps.put(userKey, COUNTRY_HASH_KEY, popularityDto.country());
        hashOps.put(userKey, RATING_HASH_KEY, String.valueOf(userImpact.getImpact()));
        zSetOps.add(LEADERBOARD_KEY, userIdStr, userImpact.getImpact());

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

    public List<UserPopularityResponseDto> getTopPopularUsers(int topN) {
        Set<String> topUserIds = zSetOps.reverseRange(LEADERBOARD_KEY, 0, topN - 1);
        return getUsers(topUserIds);
    }

    public List<UserPopularityResponseDto> getTopPopularUsers(int start, int end) {
        Set<String> topUserIds = zSetOps.reverseRange(LEADERBOARD_KEY, start - 1, end - 1);
        return getUsers(topUserIds);
    }

    private List<UserPopularityResponseDto> getUsers(Set<String> topUserIds) {
        List<UserPopularityResponseDto> result = new ArrayList<>();
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
            Double impactDouble = (Double) pipelineResults.get(index++);

            Long id = (idStr != null && !idStr.equals("null")) ? Long.valueOf(idStr) : null;
            Long userId = Long.valueOf(userIdStr);
            Long score = (impactDouble != null) ? impactDouble.longValue() : 0L;

            UserPopularityResponseDto responseDto = new UserPopularityResponseDto(id, userId, username, country, score);
            result.add(responseDto);
        }
        return result;
    }
}
