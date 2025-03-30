package school.faang.user_service.service.rating;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.rating.TopUserCacheRepository;
import school.faang.user_service.service.rating.initializer.RatingInitializer;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RatingService {

    private final TopUserCacheRepository topUserCacheRepository;
    private final UserRatingTypeService userRatingTypeService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final List<RatingInitializer> ratingInitializers;
    @Value("${rating.top-users-limit:100}")
    private Integer topUsersLimit;

    @PostConstruct
    @Transactional
    public void initCache() {
        userService.resetAllRatingScores();
        ratingInitializers.forEach(RatingInitializer::initializeRating);

        log.debug("Retrieving top {} users from DB", topUsersLimit);
        Map<UserDto, Double> usersRatingScores = userService.findAllUsersWithRatingScores(topUsersLimit);

        log.debug("Initializing top user cache with all users: {}", usersRatingScores.size());
        topUserCacheRepository.saveAll(usersRatingScores);

        List<UserDto> topUsersCached = topUserCacheRepository.getTopUsers(topUsersLimit);
        log.info("Rating service initialized");
        log.info("Top {} users in cache:\n\t{}", topUsersCached.size(), topUsersCached);
    }

    @Transactional
    public void addScore(RatingType ratingType, Long... userIds) {
        for (Long userId : userIds) {
            UserRatingType userRatingType = userRatingTypeService.findByName(ratingType);

            Double newScore = userService.addUserRatingScore(userId, userRatingType.getCost());
            log.info("User with id {} has new score {}", userId, newScore);

            boolean cacheUpdated = updateTopUserScoreInCache(userId, newScore);
            log.debug("For the user with id {} cache updated: {} with new score: {}", userId, cacheUpdated, newScore);
        }
    }

    @Transactional
    public void minusScore(RatingType ratingType, Long... userIds) {
        for (Long userId : userIds) {
            UserRatingType userRatingType = userRatingTypeService.findByName(ratingType);

            Double newScore = userService.addUserRatingScore(userId, -userRatingType.getCost());
            log.info("User with id {} has new score {}", userId, newScore);

            boolean cacheUpdated = updateTopUserScoreInCache(userId, newScore);
            log.debug("For the user with id {} cache updated: {} with new score: {}", userId, cacheUpdated, newScore);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getTopUsers(Integer limit) {
        Optional<Integer> optionalLimit = Optional.ofNullable(limit);
        return topUserCacheRepository.getTopUsers(optionalLimit.orElse(topUsersLimit));
    }

    private boolean updateTopUserScoreInCache(Long userId, Double newScore) {
        UserDto userDto = userMapper.toDto(userService.getUserById(userId));
        return topUserCacheRepository.save(userDto, newScore);
    }


}
