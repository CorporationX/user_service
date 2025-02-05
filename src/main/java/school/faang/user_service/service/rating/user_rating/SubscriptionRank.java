package school.faang.user_service.service.rating.user_rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.rating.UserRatingDto;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SubscriptionRank implements UserRank {
    private final static String RATING_TYPE_NAME = "Subscription rating";

    private final UserService userService;
    private final RatingTypeService ratingTypeService;
    private final SubscriptionService subscriptionService;

    @Override
    public boolean isApplicable(UserRatingDto ratings) {
        return ratings != null && ratings.isFolloweeRating();
    }

    @Override
    public List<UserRating> calculate(List<Long> userIds, UserRatingDto ratings) {
        validateParameters(userIds, ratings);

        UserRatingType ratingType = ratingTypeService.findByName(RATING_TYPE_NAME);

        return userIds.stream()
                .map(userId -> {
                    int followersCount = subscriptionService.getFollowersCount(userId);
                    int score = followersCount * ratingType.getCost();

                    return UserRating.builder()
                            .user(userService.getUser(userId))
                            .type(ratingType)
                            .score(score)
                            .build();
                })
                .toList();
    }
}
