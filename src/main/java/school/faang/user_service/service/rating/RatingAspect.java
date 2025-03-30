package school.faang.user_service.service.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.rating.annotation.RatingChanging;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class RatingAspect {
    private final UserContext userContext;
    private final RatingService ratingService;

    @AfterReturning("@annotation(school.faang.user_service.service.rating.annotation.RatingChanging)")
    public void changeRating(JoinPoint joinPoint) {
        Long[] affectedUsers = getAffectedUsers(joinPoint);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RatingChanging ratingChanging = signature.getMethod().getAnnotation(RatingChanging.class);
        RatingType ratingType = ratingChanging.ratingType();
        boolean positiveAction = ratingChanging.positiveAction();

        if (positiveAction) {
            ratingService.addScore(ratingType, affectedUsers);
        } else {
            ratingService.minusScore(ratingType, affectedUsers);
        }
    }

    private Long[] getAffectedUsers(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RatingChanging ratingChanging = signature.getMethod().getAnnotation(RatingChanging.class);
        RatingType ratingType = ratingChanging.ratingType();
        boolean positiveAction = ratingChanging.positiveAction();

        Object[] args = joinPoint.getArgs();

        if (ratingType == RatingType.RECOMMENDATION_RATING) {
            Recommendation recommendation = (Recommendation) args[0];
            Long author = recommendation.getAuthor().getId();
            Long receiver = recommendation.getReceiver().getId();

            return new Long[]{author, receiver};
        }

        if (ratingType == RatingType.SUBSCRIPTION_RATING) {
            Long followerId = (Long) args[0];
            Long followeeId = (Long) args[1];

            //no need to subtract rating score from follower
            if (!positiveAction) {
                return new Long[]{followeeId};
            }

            return new Long[]{followerId, followeeId};
        }

        //user may vary from the one in the context
        if (ratingType == RatingType.SKILL_RATING) {
            Long userId = (Long) args[0];
            return new Long[]{userId};
        }

        return new Long[]{userContext.getUserId()};
    }
}
