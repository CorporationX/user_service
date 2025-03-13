package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.mapper.UserActivityMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FillDB {
    private final UserActivityService userActivityService;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserActivityRepository userActivityRepository;
    private final UserActivityRedisService userActivityRedisService;
    private final UserActivityMapper userActivityMapper;
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void work() {
        log.info("Cache warm-up started");
        Pageable pageable = PageRequest.of(0, maxCachedLeaderboardSize);
        List<UserActivity> topActiveUsers = userActivityRepository.getTopActive(pageable);
        for (UserActivity userActivity : topActiveUsers) {
            userActivityRedisService.recordUserAction(userActivity,
                    userActivityMapper.toUserActivityRequestDto(userActivity));
        }
        log.info("Cache warm-up completed successfully");
        return;
       /* Random random = new Random();
        Country country = Country.builder()
                .title("country")
                .build();
        countryRepository.save(country);
        for (int i = 0; i < 10000; ++i) {
            User user = User.builder()
                    .username("username " + i)
                    .country(country)
                    .email("email.com" + i)
                    .password("password" + i)
                    .active(true)
                    .build();
            userRepository.save(user);
            UserActivityDto dto = new UserActivityDto((long) i, user.getId(), "username " + i, country.getTitle());
            userActivityService.recordUserAction(dto, random.nextInt(1000, 1000000000));
        }
        log.info("COMPLETED");*/
    }
}
