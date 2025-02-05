package school.faang.user_service.service.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.rating.UserRatingDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.repository.rating.TopUserRepository;
import school.faang.user_service.repository.rating.UserRatingRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.rating.user_rating.UserRank;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@Slf4j
@RequiredArgsConstructor
@Service
public class RatingService {

    private final UserRatingRepository userRatingRepository;
    private final RatingTypeService ratingTypeService;
    private final UserService userService;
    private final TopUserRepository topUserRepository;
    private final List<UserRank> userRanks;

    public UserRating addRating(@Validated UserRating userRating) {
        log.info("Adding user rating to database");
        return userRatingRepository.save(userRating);
    }

    public void deleteRating(@Validated UserRating userRating) {
        log.info("Deleting user rating from database");
        userRatingRepository.delete(userRating);
    }

    public UserRating addScore(@Validated Long userId, String ratingTypeName) {
        log.info("Adding score for user {} by type {}", userId, ratingTypeName);
        UserRating userRating = userRatingRepository.findByUserIdAndTypeNameIs(userId, ratingTypeName);
        UserRatingType ratingType = ratingTypeService.findByName(ratingTypeName);

        if (userRating == null) {
            User user = userService.getUser(userId);
            userRating = UserRating.builder()
                    .user(user)
                    .type(ratingType)
                    .score(0)
                    .build();
        }

        userRating.setScore(userRating.getScore() + ratingType.getCost());
        updateFullScore(userId, ratingType.getCost());
        return userRatingRepository.save(userRating);
    }

    public UserRating minusScore(@Validated Long userId, String ratingTypeName) {
        log.info("Minus score for user {} by type {}", userId, ratingTypeName);
        UserRating userRating = userRatingRepository.findByUserIdAndTypeNameIs(userId, ratingTypeName);
        UserRatingType ratingType = ratingTypeService.findByName(ratingTypeName);

        if (userRating == null) {
            log.warn("Rating with userId %s and type %s not found".formatted(userId,
                    ratingTypeName));
            User user = userService.getUser(userId);
            userRating = UserRating.builder()
                    .user(user)
                    .type(ratingType)
                    .score(0)
                    .build();
        }

        userRating.setScore(userRating.getScore() - ratingType.getCost());

        if (userRating.getScore() < 0) {
            userRating.setScore(0);
        }
        updateFullScore(userId, -ratingType.getCost());
        return userRatingRepository.save(userRating);
    }

    public List<Long> getTopUsers() {
        Map<Long, Double> topUsers = topUserRepository.getTopUsersWithScores();

        if (topUsers == null || topUsers.isEmpty()) {
            List<UserRating> userRatings = calculateAllRatings();
            updateFullRating(calculateFullScores(userRatings));
            topUsers = topUserRepository.getTopUsersWithScores();
        }

        return topUsers.entrySet().stream()
                .sorted(Comparator.comparingDouble(longDoubleEntry -> -longDoubleEntry.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private void updateFullRating(Map<Long, Integer> scoreMap) {
        log.info("Updating full rating");
        scoreMap.forEach((key, value) -> {
            log.debug("Updating user rating {}", key);
            topUserRepository.save(key, value.doubleValue());
        });
    }

    private Map<Long, Integer> calculateFullScores(List<UserRating> userRatings) {
        log.info("Calculating full scores for user ratings");
        return userRatings.stream()
                .collect(Collectors.groupingBy(rating -> rating.getUser().getId(),
                        Collectors.summingInt(UserRating::getScore)));

    }

    private List<UserRating> calculateAllRatings() {
        log.info("Calculating all ratings");
        UserRatingDto ratingDto = UserRatingDto.builder()
                .followeeRating(true)
                .goalRating(true)
                .menteeRating(true)
                .premiumRating(true)
                .skillRating(true)
                .build();

        List<Long> allUsersId = userService.findAllUsers().stream()
                .map(User::getId)
                .toList();

        return userRanks.stream()
                .filter(userRank -> userRank.isApplicable(ratingDto))
                .flatMap(userRank -> userRank.calculate(allUsersId, ratingDto).stream())
                .toList();

    }

    private boolean updateFullScore(@Validated Long userId, int changeScore) {
        int score = (int) topUserRepository.getTopUserScore(userId);
        score += changeScore;
        return topUserRepository.save(userId, (double) score);
    }
}
