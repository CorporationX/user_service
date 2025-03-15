package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.entity.leaderboard.UserImpact;
import school.faang.user_service.mapper.LeaderboardMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserPopularityRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPopularityService {
    private final UserPopularityRepository userPopularityRepository;
    private final UserRepository userRepository;
    private final UserPopularityRedisService userPopularityRedisService;
    private final LeaderboardMapper leaderboardMapper;
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void recordUserImpact(UserPopularityRequestDto userImpactRequestDto, int impactScore) {
        Optional<UserImpact> optionalUserActivity = userPopularityRepository.findById(userImpactRequestDto.id());
        UserImpact userImpact = new UserImpact();
        if (optionalUserActivity.isEmpty()) {
            userImpact.setRating(impactScore);
            userImpact.setUser(userRepository.findById(userImpactRequestDto.userId()).orElseThrow(
                    () -> new IllegalArgumentException("UserId doesn't exists in DB")));
        } else {
            userImpact = optionalUserActivity.get();
            userImpact.setRating(userImpact.getRating() + impactScore);
        }
        userPopularityRepository.save(userImpact);
        userPopularityRedisService.recordUserImpact(userImpact, userImpactRequestDto);
        log.info("Updated popularity rating for user with id {} is {}", userImpactRequestDto.userId(), userImpact.getRating());
    }

    public List<UserPopularityResponseDto> getTopPopularUsers(int topN) {
        if (topN <= maxCachedLeaderboardSize) {
            return userPopularityRedisService.getTopImpactUsers(topN);
        }
        Pageable pageable = PageRequest.of(0, topN);
        return leaderboardMapper.toUserPopularityResponseDtoList(userPopularityRepository.getTopPopular(pageable));
    }

    public List<UserPopularityResponseDto> getTopPopularUsers(int start, int end) {
        if (start >= maxCachedLeaderboardSize && maxCachedLeaderboardSize <= end) {
            return userPopularityRedisService.getTopImpactUsers(start, end);
        }
        Pageable pageable = PageRequest.of(start, end - start + 1);
        return leaderboardMapper.toUserPopularityResponseDtoList(userPopularityRepository.getTopPopular(pageable));
    }
}
