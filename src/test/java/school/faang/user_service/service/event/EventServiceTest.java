package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventParticipationService eventParticipationService;

    private final long EVENT_ID_ONE = 1L;
    private final long EVENT_ID_TWO = 2L;

    private final long USER_ID_IS_ONE = 1L;

    @Nested
    class PositiveTests {

        private User user;
        private Event plannedEvent;
        private Event canceledEvent;

        @BeforeEach
        void init() {
            plannedEvent = Event.builder()
                    .id(EVENT_ID_ONE)
                    .status(EventStatus.PLANNED)
                    .build();

            canceledEvent = Event.builder()
                    .id(EVENT_ID_TWO)
                    .status(EventStatus.CANCELED)
                    .build();

            List<Event> events = new ArrayList<>();
            events.add(plannedEvent);
            events.add(canceledEvent);

            user = User.builder()
                    .id(USER_ID_IS_ONE)
                    .ownedEvents(events)
                    .build();
        }

        @Nested
        class DeactivatePlanningUserEventsAndDeleteEventMethod {
            @Test
            @DisplayName("Если у юзера есть запланированный ивент в списке, тогда удаляем его")
            void whenUserHasPlannedEventThenCancelItAndDeleteFromListUserEvents() {
                eventService.deactivatePlanningUserEventsAndDeleteEvent(user);

                assertEquals(EventStatus.CANCELED, plannedEvent.getStatus());

                verify(eventParticipationService)
                        .deleteParticipantsFromEvent(plannedEvent);
                verify(eventRepository)
                        .deleteById(plannedEvent.getId());

                assertFalse(user.getOwnedEvents().contains(plannedEvent));
                assertTrue(user.getOwnedEvents().contains(canceledEvent));
            }
        }
    }
}