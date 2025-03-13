package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserActivityDto;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.mapper.UserActivityMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;
    private final UserActivityRedisService userActivityRedisService;
    private final UserRepository userRepository;
    private final UserActivityMapper userActivityMapper;
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void recordUserAction(UserActivityDto userDto, int rating) {
        UserActivity userActivity = userActivityRepository.findByUserId(userDto.userId());
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setRating(rating);
            userActivity.setUser(userRepository.findById(userDto.userId()).orElseThrow());
        } else {
            userActivity.setRating(userActivity.getRating() + rating);
        }
        userActivityRepository.save(userActivity);
        userActivityRedisService.recordUserAction(userActivity, userDto);
        log.info("Updated rating for user with id {} is {}", userDto.userId(), userActivity.getRating());
    }

    public List<UserActivityDto> getTopActiveUsers(int topN) {
        /*int cachedBorder = Math.min(topN, maxCachedLeaderboardSize);
        List<UserActivity> topActiveUsers = userActivityRedisService.getTopActiveUsers(cachedBorder);
        return topActiveUsers;*/
        Pageable pageable = PageRequest.of(0, topN);
        return userActivityMapper.toUserActivityDtoList(userActivityRepository.getTopActive(pageable));
    }

    public List<UserActivityDto> getTopActiveUsers(int start, int end) {
        Pageable pageable = PageRequest.of(start, end);
        return userActivityMapper.toUserActivityDtoList(userActivityRepository.getTopActive(pageable));
    }
}
