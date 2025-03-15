package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.mapper.UserActivityMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;

import java.util.List;
import java.util.Optional;

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

    public void recordUserAction(UserActivityRequestDto userActivityDto, int rating) {
        Optional<UserActivity> optionalUserActivity = userActivityRepository.findById(userActivityDto.id());
        UserActivity userActivity;
        if (optionalUserActivity.isEmpty()) {
            userActivity = new UserActivity();
            userActivity.setRating(rating);
            userActivity.setUser(userRepository.findById(userActivityDto.userId()).orElseThrow(
                    () -> new IllegalArgumentException("UserId doesn't exists in DB")));
        } else {
            userActivity = optionalUserActivity.get();
            userActivity.setRating(userActivity.getRating() + rating);
        }
        userActivityRepository.save(userActivity);
        //userActivityRedisService.recordUserAction(userActivity, userActivityDto);
        log.info("Updated rating for user with id {} is {}", userActivityDto.userId(), userActivity.getRating());
    }

    public List<UserActivityResponseDto> getTopActiveUsers(int topN) {
        int cachedBorder = Math.min(topN, maxCachedLeaderboardSize);
        List<UserActivityResponseDto> topActiveUsers = userActivityRedisService.getTopActiveUsers(cachedBorder);
        return topActiveUsers;
//        Pageable pageable = PageRequest.of(0, topN);
//        return userActivityMapper.toUserActivityResponseDtoList(userActivityRepository.getTopActive(pageable));
    }

    public List<UserActivityResponseDto> getTopActiveUsers(int start, int end) {
        Pageable pageable = PageRequest.of(start, end);
        return userActivityMapper.toUserActivityResponseDtoList(userActivityRepository.getTopActive(pageable));
    }
}