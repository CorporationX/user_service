package school.faang.user_service.service.rating.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.rating.UserRatingTypeService;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.user.UserService;

/**
 * This class is responsible for initializing the rating of all users based on recommendations, given and received.
 */
@RequiredArgsConstructor
@Component
public class RecommendationRatingInitializer implements RatingInitializer {
    private static final RatingType RATING_TYPE = RatingType.RECOMMENDATION_RATING;
    private final RecommendationService recommendationService;
    private final UserService userService;
    private final UserRatingTypeService userRatingTypeService;

    @Override
    public void initializeRating() {
        UserRatingType userRatingType = userRatingTypeService.findByName(RATING_TYPE);

        recommendationService.getNumberOfGivenRecommendationsPerUser().forEach((userId, numOfRecommendations) -> {
            userService.addUserRatingScore(userId, numOfRecommendations * userRatingType.getCost());
        });

        recommendationService.getNumberOfReceivedRecommendationsPerUser().forEach((userId, numOfRecommendations) -> {
            userService.addUserRatingScore(userId, numOfRecommendations * userRatingType.getCost());
         });

    }

}
