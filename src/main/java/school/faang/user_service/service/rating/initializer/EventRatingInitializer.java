package school.faang.user_service.service.rating.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.rating.UserRatingTypeService;
import school.faang.user_service.service.user.UserService;

/**
 * This class is responsible for initializing the rating of all users based on events.
 */
@RequiredArgsConstructor
@Component
public class EventRatingInitializer implements RatingInitializer {
    private static final RatingType RATING_TYPE = RatingType.EVENT_RATING;
    private final EventParticipationService eventParticipationService;
    private final EventService eventService;
    private final UserService userService;
    private final UserRatingTypeService userRatingTypeService;

    @Override
    public void initializeRating() {
        UserRatingType userRatingType = userRatingTypeService.findByName(RATING_TYPE);

        eventParticipationService.getNumberOfVisitedEventsPerUser().forEach((userId, numOfEvents) -> {
            userService.addUserRatingScore(userId, numOfEvents * userRatingType.getCost());
        });

        eventService.getNumberOfOwnedEventsPerUser().forEach((userId, numOfEvents) -> {
            userService.addUserRatingScore(userId, numOfEvents * userRatingType.getCost());
        });

    }

}
