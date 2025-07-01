package school.faang.user_service.aspect.score;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.score.ScoreTrackingService;

import static school.faang.user_service.aspect.util.AspectUtils.extractArgument;

@Aspect
@Component
@RequiredArgsConstructor
public class ScoreTrackingAspect {

    private final ScoreTrackingService scoreTrackingService;

    @Around("@annotation(trackActionScore)")
    public Object trackScore(ProceedingJoinPoint joinPoint, TrackActionScore trackActionScore) throws Throwable {
        Object result = joinPoint.proceed();

        ScoreActionType type = trackActionScore.value();
        switch (type) {
            case COMPLETE_GOAL -> {
                Goal goal = extractArgument(joinPoint, Goal.class);
                scoreTrackingService.trackAfterCompleteGoal(goal);
            }
            case COMPLETE_EVENT -> {
                Event event = extractArgument(joinPoint, Event.class);
                scoreTrackingService.trackAfterCompleteEvent(event);
            }
        }

        return result;
    }
}
