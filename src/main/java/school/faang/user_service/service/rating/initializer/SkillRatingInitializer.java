package school.faang.user_service.service.rating.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.rating.UserRatingTypeService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

/**
 * This class is responsible for initializing the rating of all users based on skills acquired.
 */
@RequiredArgsConstructor
@Component
public class SkillRatingInitializer implements RatingInitializer {
    private static final RatingType RATING_TYPE = RatingType.SKILL_RATING;
    private final SkillService skillService;
    private final UserService userService;
    private final UserRatingTypeService userRatingTypeService;

    @Override
    public void initializeRating() {
        UserRatingType userRatingType = userRatingTypeService.findByName(RATING_TYPE);

        skillService.getNumberOfSkillsPerUser().forEach((userId, numOfSkills) -> {
            userService.addUserRatingScore(userId, numOfSkills * userRatingType.getCost());
        });

    }

}