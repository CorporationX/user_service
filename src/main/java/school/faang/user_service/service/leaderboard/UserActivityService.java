package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.mapper.LeaderboardMapper;
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
    private final LeaderboardMapper userActivityMapper;
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void recordUserAction(UserActivityRequestDto userActivityRequestDto, int rating) {
        Optional<UserActivity> optionalUserActivity = userActivityRepository.findById(userActivityRequestDto.id());
        UserActivity userActivity = new UserActivity();
        if (optionalUserActivity.isEmpty()) {
            userActivity.setRating(rating);
            userActivity.setUser(userRepository.findById(userActivityRequestDto.userId()).orElseThrow(
                    () -> new IllegalArgumentException("UserId doesn't exists in DB")));
        } else {
            userActivity = optionalUserActivity.get();
            userActivity.setRating(userActivity.getRating() + rating);
        }
        userActivityRepository.save(userActivity);
        userActivityRedisService.recordUserAction(userActivity, userActivityRequestDto);
        log.info("Updated activity rating for user with id {} is {}", userActivityRequestDto.userId(), userActivity.getRating());
    }

    public List<UserActivityResponseDto> getTopActiveUsers(int topN) {
        if (topN <= maxCachedLeaderboardSize) {
            return userActivityRedisService.getTopActiveUsers(topN);
        }
        Pageable pageable = PageRequest.of(0, topN);
        return userActivityMapper.toUserActivityResponseDtoList(userActivityRepository.getTopActive(pageable));
    }

    public List<UserActivityResponseDto> getTopActiveUsers(int start, int end) {
        if (start >= maxCachedLeaderboardSize && maxCachedLeaderboardSize <= end) {
            return userActivityRedisService.getTopActiveUsers(start, end);
        }
        Pageable pageable = PageRequest.of(start, end - start + 1);
        return userActivityMapper.toUserActivityResponseDtoList(userActivityRepository.getTopActive(pageable));
    }
}