package school.faang.user_service.service.rating.initializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.rating.UserRatingTypeService;
import school.faang.user_service.service.user.UserService;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRatingInitializerTest {

    private static final RatingType RATING_TYPE = RatingType.EVENT_RATING;

    private static final Double RATING_COST = 10.0;

    private static final Long userId = 1L;

    private static final Integer numOfVisitedEvents = 2;

    private static final Integer numOfOwnedEvents = 3;

    @Mock
    private EventParticipationService eventParticipationService;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private UserRatingTypeService userRatingTypeService;

    @InjectMocks
    private EventRatingInitializer eventRatingInitializer;

    @BeforeEach
    void setUp() {
        UserRatingType userRatingType = new UserRatingType();
        userRatingType.setName(RATING_TYPE);
        userRatingType.setCost(RATING_COST);

        when(userRatingTypeService.findByName(RatingType.EVENT_RATING)).thenReturn(userRatingType);
        when(eventParticipationService.getNumberOfVisitedEventsPerUser()).thenReturn(Map.of(userId, numOfVisitedEvents));
        when(eventService.getNumberOfOwnedEventsPerUser()).thenReturn(Map.of(userId, numOfOwnedEvents));
    }

    @Test
    void testInitializeRating() {
        eventRatingInitializer.initializeRating();

        verify(userService, times(1)).addUserRatingScore(1L, numOfVisitedEvents * RATING_COST);
        verify(userService, times(1)).addUserRatingScore(1L, numOfOwnedEvents * RATING_COST);
    }
}