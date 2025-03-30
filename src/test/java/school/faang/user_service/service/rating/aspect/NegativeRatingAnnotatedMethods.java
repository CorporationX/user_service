package school.faang.user_service.service.rating.aspect;

import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.rating.annotation.RatingChanging;

/**Class used to test {@link school.faang.user_service.service.rating.RatingAspect} providing different
 * methods annotated with {@link RatingChanging} annotation and positiveAction parameter = false*/
public class NegativeRatingAnnotatedMethods {

    @RatingChanging(ratingType = RatingType.GOAL_RATING, positiveAction = false)
    public void goalRatingMethod() {}

    @RatingChanging(ratingType = RatingType.PREMIUM_RATING, positiveAction = false)
    public void premiumRatingMethod() {}

    @RatingChanging(ratingType = RatingType.SKILL_RATING, positiveAction = false)
    public void skillRatingMethod() {}

    @RatingChanging(ratingType = RatingType.SUBSCRIPTION_RATING, positiveAction = false)
    public void subscriptionRatingMethod() {}

    @RatingChanging(ratingType = RatingType.RECOMMENDATION_RATING, positiveAction = false)
    public void recommendationRatingMethod() {}

    @RatingChanging(ratingType = RatingType.EVENT_RATING, positiveAction = false)
    public void eventRatingMethod() {}
    
}
