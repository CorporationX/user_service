package school.faang.user_service.service.rating.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.rating.UserRatingTypeService;
import school.faang.user_service.service.user.UserService;

/**
 * This class is responsible for initializing the rating of all users based on goals.
 */
@RequiredArgsConstructor
@Component
public class GoalRatingInitializer implements RatingInitializer {
    private static final RatingType RATING_TYPE = RatingType.GOAL_RATING;
    private final GoalService goalService;
    private final UserService userService;
    private final UserRatingTypeService userRatingTypeService;

    @Override
    public void initializeRating() {
        UserRatingType userRatingType = userRatingTypeService.findByName(RATING_TYPE);

        goalService.findNumOfGoalsPerUser().forEach((userId, numOfGoals) -> {
            userService.addUserRatingScore(userId, numOfGoals * userRatingType.getCost());
        });

    }

}
