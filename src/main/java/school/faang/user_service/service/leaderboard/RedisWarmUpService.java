package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.entity.leaderboard.UserImpact;
import school.faang.user_service.mapper.LeaderboardMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;
import school.faang.user_service.repository.leaderboard.UserPopularityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisWarmUpService {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserActivityRepository userActivityRepository;
    private final UserPopularityRepository userPopularityRepository;
    private final UserActivityRedisService userActivityRedisService;
    private final UserPopularityRedisService userPopularityRedisService;
    private final LeaderboardMapper leaderboardMapper;
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void warmUpCache() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        log.info("Cache warm-up started");
        Pageable pageable = PageRequest.of(0, maxCachedLeaderboardSize);
        List<UserActivity> topActiveUsers = userActivityRepository.getTopActive(pageable);
        for (UserActivity userActivity : topActiveUsers) {
            userActivityRedisService.recordUserAction(userActivity,
                    leaderboardMapper.toUserActivityRequestDto(userActivity));
        }
        List<UserImpact> topPopularUsers = userPopularityRepository.getTopPopular(pageable);
        for (UserImpact userImpact : topPopularUsers) {
            userPopularityRedisService.recordUserImpact(userImpact,
                    leaderboardMapper.toUserPopularityRequestDto(userImpact));
        }
        log.info("Cache warm-up completed successfully");
        //fill();
    }

    private static final int BATCH_SIZE = 25_000_000;
    private final CountryRepository countryRepository;
    private final UserRepository userRepository;
    private final UserPopularityService userPopularityService;

    public void fill() {

        Random random = new Random();
        countryRepository.save(new Country(1L, "country", new ArrayList<>()));
        Country country = countryRepository.findById(1L).get();

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int batch = 0; batch < 8; ++batch) {
            int startIndex = batch * BATCH_SIZE;
            int endIndex = (batch + 1) * BATCH_SIZE;

            executorService.submit(() -> processBatch(startIndex, endIndex, country, random));
        }

        executorService.shutdown();
    }

    private void processBatch(int startIndex, int endIndex, Country country, Random random) {
        for (int i = startIndex; i < endIndex; ++i) {
            User user = User.builder()
                    .username("username1 " + i)
                    .country(country)
                    .email("email.com1" + i)
                    .password("password1" + i)
                    .active(true)
                    .build();
            userRepository.save(user);
            UserPopularityRequestDto dto = new UserPopularityRequestDto((long) i, user.getId(), "username1 " + i, country.getTitle());
            userPopularityService.recordUserImpact(dto, random.nextInt(1000, 1_000_000_000));
        }
    }
}