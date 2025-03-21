package school.faang.user_service.service.leaderboard;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserActionDto;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;
import school.faang.user_service.dto.leaderboard.UserActivityResponseDto;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.mapper.leaderboard.UserActivityMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;
import school.faang.user_service.utils.validationUtils.leaderboard.UserActivityValidation;

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

    public void recordUserAction(UserActivityRequestDto userActivityRequestDto, UserActionDto userActionDto) {
        UserActivityValidation.validateUserActivityRequestDto(userActivityRequestDto);
        UserActivityValidation.validateUserActionDto(userActionDto);
        Optional<UserActivity> optionalUserActivity = userActivityRepository.findById(userActivityRequestDto.id());
        UserActivity userActivity = new UserActivity();

        if (optionalUserActivity.isEmpty()) {
            userActivity.setRating(userActionDto.getRating());
            userActivity.setUser(userRepository.findById(userActivityRequestDto.userId()).orElseThrow(
                    () -> new EntityNotFoundException("UserId " + userActivityRequestDto.userId() + " not found")));
        } else {
            userActivity = optionalUserActivity.get();
            userActivity.setRating(userActivity.getRating() + userActionDto.getRating());
        }
        userActivityRepository.save(userActivity);
        userActivityRedisService.recordUserAction(userActivity, userActivityRequestDto);
        log.info("Updated activity rating for user with id {} is {}", userActivityRequestDto.userId(), userActivity.getRating());
    }

    public List<UserActivityResponseDto> getTopActiveUsers(int topN) {
        UserActivityValidation.validateTopN(topN);
        if (topN <= maxCachedLeaderboardSize) {
            return userActivityRedisService.getTopActiveUsers(topN);
        }
        Pageable pageable = PageRequest.of(0, topN);
        return userActivityMapper.toUserActivityResponseDtoList(userActivityRepository.getTopActive(pageable));
    }

    public List<UserActivityResponseDto> getTopActiveUsers(int start, int end) {
        UserActivityValidation.validateTopRange(start, end);
        if (end <= maxCachedLeaderboardSize) {
            return userActivityRedisService.getTopActiveUsers(start, end);
        }
        int size = end - start + 1;
        Pageable pageable = PageRequest.of(start / size, size);
        return userActivityMapper.toUserActivityResponseDtoList(userActivityRepository.getTopActive(pageable));
    }
}