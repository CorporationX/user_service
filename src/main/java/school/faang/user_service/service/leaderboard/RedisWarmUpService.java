package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.entity.leaderboard.UserPopularity;
import school.faang.user_service.mapper.leaderboard.UserActivityMapper;
import school.faang.user_service.mapper.leaderboard.UserPopularityMapper;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;
import school.faang.user_service.repository.leaderboard.UserPopularityRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisWarmUpService {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserActivityRepository userActivityRepository;
    private final UserPopularityRepository userPopularityRepository;
    private final UserActivityRedisService userActivityRedisService;
    private final UserPopularityRedisService userPopularityRedisService;
    private final UserActivityMapper userActivityMapper;
    private final UserPopularityMapper userPopularityMapper;
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void warmUpCache() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
        log.info("Leaderboard cache warm-up started");
        Pageable pageable = PageRequest.of(0, maxCachedLeaderboardSize);
        List<UserActivity> topActiveUsers = userActivityRepository.getTopActive(pageable);
        for (UserActivity userActivity : topActiveUsers) {
            userActivityRedisService.recordUserAction(userActivity,
                    userActivityMapper.toUserActivityRequestDto(userActivity));
        }
        List<UserPopularity> topPopularUsers = userPopularityRepository.getTopPopular(pageable);
        for (UserPopularity userImpact : topPopularUsers) {
            userPopularityRedisService.recordUserImpact(userImpact,
                    userPopularityMapper.toUserPopularityRequestDto(userImpact));
        }
        log.info("Leaderboard cache warm-up completed successfully");
    }
}