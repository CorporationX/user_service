package school.faang.user_service.service.leaderboard;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.UserImpactDto;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;
import school.faang.user_service.dto.leaderboard.UserPopularityResponseDto;
import school.faang.user_service.entity.leaderboard.UserPopularity;
import school.faang.user_service.mapper.leaderboard.UserPopularityMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.leaderboard.UserPopularityRepository;
import school.faang.user_service.utils.validationUtils.leaderboard.UserPopularityValidation;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPopularityService {
    private final UserPopularityRepository userPopularityRepository;
    private final UserRepository userRepository;
    private final UserPopularityRedisService userPopularityRedisService;
    private final UserPopularityMapper userPopularityMapper;
    @Value("${app.leaderboard.max-cached-size}")
    private int maxCachedLeaderboardSize;

    public void recordUserImpact(UserPopularityRequestDto userPopularityRequestDto, UserImpactDto userImpactDto) {
        UserPopularityValidation.validateUserPopularityRequestDto(userPopularityRequestDto);
        UserPopularityValidation.validateUserImpactDto(userImpactDto);
        Optional<UserPopularity> optionalUserActivity = userPopularityRepository.findById(userPopularityRequestDto.id());
        UserPopularity userPopularity = new UserPopularity();
        if (optionalUserActivity.isEmpty()) {
            userPopularity.setImpact(userImpactDto.getImpactScore());
            userPopularity.setUser(userRepository.findById(userPopularityRequestDto.userId()).orElseThrow(
                    () -> new EntityNotFoundException("UserId " + userPopularityRequestDto.userId() + " not found")));
        } else {
            userPopularity = optionalUserActivity.get();
            userPopularity.setImpact(userPopularity.getImpact() + userImpactDto.getImpactScore());
        }
        userPopularityRepository.save(userPopularity);
        userPopularityRedisService.recordUserImpact(userPopularity, userPopularityRequestDto);
        log.info("Updated popularity rating for user with id {} is {}", userPopularityRequestDto.userId(), userPopularity.getImpact());
    }

    public List<UserPopularityResponseDto> getTopPopularUsers(int topN) {
        UserPopularityValidation.validateTopN(topN);
        if (topN <= maxCachedLeaderboardSize) {
            return userPopularityRedisService.getTopPopularUsers(topN);
        }
        Pageable pageable = PageRequest.of(0, topN);
        return userPopularityMapper.toUserPopularityResponseDtoList(userPopularityRepository.getTopPopular(pageable));
    }

    public List<UserPopularityResponseDto> getTopPopularUsers(int start, int end) {
        UserPopularityValidation.validateTopRange(start, end);
        if (end <= maxCachedLeaderboardSize) {
            return userPopularityRedisService.getTopPopularUsers(start, end);
        }
        int size = end - start + 1;
        Pageable pageable = PageRequest.of(start / size, size);
        return userPopularityMapper.toUserPopularityResponseDtoList(userPopularityRepository.getTopPopular(pageable));
    }
}
