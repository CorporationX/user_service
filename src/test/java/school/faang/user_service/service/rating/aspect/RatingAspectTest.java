package school.faang.user_service.service.rating.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.rating.RatingAspect;
import school.faang.user_service.service.rating.RatingService;
import school.faang.user_service.service.rating.annotation.RatingChanging;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingAspectTest {

    @Mock
    private UserContext userContext;

    @Mock
    private RatingService ratingService;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private RatingAspect ratingAspect;

    private abstract class RatingChangingForTest implements RatingChanging{} //this is a workaround for mocking RatingChanging annotation

    /**Map used to provide {@link Method} instance annotated with {@link RatingChanging}*/
    private static Map<RatingType, Method> methodsPerPositiveRatingType;

    /**Map used to provide {@link Method} instance annotated with {@link RatingChanging}*/
    private static Map<RatingType, Method> methodsPerNegativeRatingType;

    /**Args that ratingAspect will use to determine affectedUsers.<br/>If no RatingType is specified, by default userContext is used*/
    private static Map<RatingType, Object[]> argsPerRatingsMap;

    /**Users actually affected by a positive {@link RatingChanging}. Used to verify the call to {@link RatingService}.
     * If a ratingType is not specified, by default user contained in userContext will be used*/
    private static Map<RatingType, Long[]> affectedUsersPerRatingsMap;

    /**Users actually affected by a negative {@link RatingChanging}. Used to verify the call to {@link RatingService}.
     * If a ratingType is not specified, by default user contained in affectedUsersPerRatingsMap will be used for verification.
     * If that is not specified either, by default user contained in userContext will be used*/
    private static Map<RatingType, Long[]> affectedUsersPerNegativeRatingsMap;

    private static Recommendation recommendation;
    private static User userA = User.builder().id(1L).build();
    private static User userB = User.builder().id(2L).build();

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        when(userContext.getUserId()).thenReturn(userA.getId());
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        recommendation = Recommendation.builder().author(userA).receiver(userB).build();

        methodsPerPositiveRatingType = Map.of(
                RatingType.GOAL_RATING, PositiveRatingAnnotatedMethods.class.getMethod("goalRatingMethod"),
                RatingType.PREMIUM_RATING, PositiveRatingAnnotatedMethods.class.getMethod("premiumRatingMethod"),
                RatingType.SKILL_RATING, PositiveRatingAnnotatedMethods.class.getMethod("skillRatingMethod"),
                RatingType.SUBSCRIPTION_RATING, PositiveRatingAnnotatedMethods.class.getMethod("subscriptionRatingMethod"),
                RatingType.RECOMMENDATION_RATING, PositiveRatingAnnotatedMethods.class.getMethod("recommendationRatingMethod"),
                RatingType.EVENT_RATING, PositiveRatingAnnotatedMethods.class.getMethod("eventRatingMethod")
        );
        methodsPerNegativeRatingType = Map.of(
                RatingType.GOAL_RATING, NegativeRatingAnnotatedMethods.class.getMethod("goalRatingMethod"),
                RatingType.PREMIUM_RATING, NegativeRatingAnnotatedMethods.class.getMethod("premiumRatingMethod"),
                RatingType.SKILL_RATING, NegativeRatingAnnotatedMethods.class.getMethod("skillRatingMethod"),
                RatingType.SUBSCRIPTION_RATING, NegativeRatingAnnotatedMethods.class.getMethod("subscriptionRatingMethod"),
                RatingType.RECOMMENDATION_RATING, NegativeRatingAnnotatedMethods.class.getMethod("recommendationRatingMethod"),
                RatingType.EVENT_RATING, NegativeRatingAnnotatedMethods.class.getMethod("eventRatingMethod")
        );
        argsPerRatingsMap = Map.ofEntries(
                Map.entry(RatingType.RECOMMENDATION_RATING, new Object[]{recommendation}),
                Map.entry(RatingType.SUBSCRIPTION_RATING, new Long[]{userA.getId(), userB.getId()}),
                Map.entry(RatingType.SKILL_RATING, new Long[]{userB.getId()})
        );
        affectedUsersPerRatingsMap = Map.ofEntries(
                Map.entry(RatingType.RECOMMENDATION_RATING, new Long[]{userA.getId(), userB.getId()}),
                Map.entry(RatingType.SUBSCRIPTION_RATING, new Long[]{userA.getId(), userB.getId()}),
                Map.entry(RatingType.SKILL_RATING, new Long[]{userB.getId()})
        );
        affectedUsersPerNegativeRatingsMap = Map.ofEntries(
                Map.entry(RatingType.SUBSCRIPTION_RATING, new Long[]{userB.getId()})
        );
    }

    @Test
    void testChangeRating() {
        Long[] userIds;
        List<Boolean> booleans = List.of(Boolean.TRUE, Boolean.FALSE);

        //go through each rating type and test it
        for (RatingType ratingType : RatingType.values()) {
            when(joinPoint.getArgs()).thenReturn(argsPerRatingsMap.get(ratingType));

            for (Boolean positiveAction : booleans) {
                userIds = new Long[]{userContext.getUserId()};

                if (positiveAction) {
                    when(methodSignature.getMethod()).thenReturn(methodsPerPositiveRatingType.get(ratingType));
                } else {
                    when(methodSignature.getMethod()).thenReturn(methodsPerNegativeRatingType.get(ratingType));
                }

                ratingAspect.changeRating(joinPoint);

                if (affectedUsersPerRatingsMap.containsKey(ratingType)) {
                    userIds = affectedUsersPerRatingsMap.get(ratingType);
                }

                if (positiveAction) {
                    verify(ratingService, times(1)).addScore(ratingType, userIds);
                } else {
                    if (affectedUsersPerNegativeRatingsMap.containsKey(ratingType)) {
                        userIds = affectedUsersPerNegativeRatingsMap.get(ratingType);
                    }
                    verify(ratingService, times(1)).minusScore(ratingType, userIds);
                }

            }

        }
    }
}