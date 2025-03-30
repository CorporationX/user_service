package school.faang.user_service.service.rating.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.rating.UserRatingTypeService;
import school.faang.user_service.service.user.UserService;

/**
 * This class is responsible for initializing the rating of all users based on premium subscription.
 */
@RequiredArgsConstructor
@Component
public class PremiumRatingInitializer implements RatingInitializer {
    private static final RatingType RATING_TYPE = RatingType.PREMIUM_RATING;
    private final PremiumService premiumService;
    private final UserService userService;
    private final UserRatingTypeService userRatingTypeService;

    @Override
    public void initializeRating() {
        UserRatingType userRatingType = userRatingTypeService.findByName(RATING_TYPE);

        premiumService.getPremiumUsers().forEach(userId -> {
            userService.addUserRatingScore(userId, userRatingType.getCost());
        });

    }

}
