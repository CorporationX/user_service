package school.faang.user_service.service.rating.user_rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.rating.UserRatingDto;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PremiumRank implements UserRank {
    private final static String RATING_TYPE_NAME = "Premium rating";

    private final UserService userService;
    private final RatingTypeService ratingTypeService;
    private final PremiumService premiumService;

    @Override
    public boolean isApplicable(UserRatingDto ratings) {
        return ratings != null && ratings.isPremiumRating();
    }

    @Override
    public List<UserRating> calculate(List<Long> userIds, UserRatingDto ratings) {
        validateParameters(userIds, ratings);

        UserRatingType ratingType = ratingTypeService.findByName(RATING_TYPE_NAME);

        return userIds.stream()
                .map(userId -> {
                    int score = 0;

                    if (premiumService.checkPremiumByUserId(userId)) {
                        score = ratingType.getCost();
                    }

                    return UserRating.builder()
                            .user(userService.getUser(userId))
                            .type(ratingType)
                            .score(score)
                            .build();
                })
                .toList();
    }
}
