package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserDto;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.enums.UserAction;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;
    private final UserActivityRedisService userActivityRedisService;
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void recordUserAction(UserDto userDto, int rating) {
        UserActivity userActivity = userActivityRepository.findByUserId(userDto.userId());
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setRating(rating);
            userActivity.setUsername(userDto.username());
            userActivity.setCountry(userDto.country());
            userActivity.setUserId(userDto.userId());
        } else {
            userActivity.setRating(userActivity.getRating() + rating);
        }
        userActivityRepository.save(userActivity);
        userActivityRedisService.recordUserAction(userActivity);
        log.info("Updated rating for user with id {} is {}", userDto.userId(), userActivity.getRating());
    }

    public List<UserActivity> getTopActiveUsers(int topN) {
        int cachedBorder = Math.min(topN, maxCachedLeaderboardSize);
        List<UserActivity> topActiveUsers = userActivityRedisService.getTopActiveUsers(cachedBorder);
        return topActiveUsers;
        /*Pageable pageable = PageRequest.of(cachedBorder, topN);
        System.out.println(topActiveUsers);
        topActiveUsers.addAll(userActivityRepository.getTopActive(pageable));
        System.out.println(userActivityRepository.getTopActive(pageable));
        return topActiveUsers;*/
    }

    public List<UserActivity> getTopActiveUsers(int start, int end) {
        Pageable pageable = PageRequest.of(start, end);
        return userActivityRepository.getTopActive(pageable);
    }
}
