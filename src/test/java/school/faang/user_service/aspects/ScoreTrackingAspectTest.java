package school.faang.user_service.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.aspect.score.ScoreTrackingAspect;
import school.faang.user_service.aspect.score.TrackActionScore;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.score.ScoreTrackingService;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ScoreTrackingAspectTest {

    @Mock
    private ScoreTrackingService scoreTrackingService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private ScoreTrackingAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new ScoreTrackingAspect(scoreTrackingService);
    }

    @Test
    void trackScore_shouldHandleGoal() throws Throwable {
        Goal goal = new Goal();
        Object[] args = { "other", goal };
        Mockito.when(joinPoint.getArgs()).thenReturn(args);
        Mockito.when(joinPoint.proceed()).thenReturn("RESULT");

        TrackActionScore annotation = createAnnotation(ScoreActionType.COMPLETE_GOAL);

        Object result = aspect.trackScore(joinPoint, annotation);

        assertThat(result).isEqualTo("RESULT");
        Mockito.verify(scoreTrackingService).trackAfterCompleteGoal(goal);
        Mockito.verifyNoMoreInteractions(scoreTrackingService);
    }

    @Test
    void trackScore_shouldHandleEvent() throws Throwable {
        Event event = new Event();
        Object[] args = { event };
        Mockito.when(joinPoint.getArgs()).thenReturn(args);
        Mockito.when(joinPoint.proceed()).thenReturn(null);

        TrackActionScore annotation = createAnnotation(ScoreActionType.COMPLETE_EVENT);

        aspect.trackScore(joinPoint, annotation);

        Mockito.verify(scoreTrackingService).trackAfterCompleteEvent(event);
    }

    private TrackActionScore createAnnotation(ScoreActionType type) {
        return new TrackActionScore() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return TrackActionScore.class;
            }

            @Override
            public ScoreActionType value() {
                return type;
            }
        };
    }
}
