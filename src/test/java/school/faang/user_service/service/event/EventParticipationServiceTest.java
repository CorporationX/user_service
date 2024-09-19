package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    private static final long EVENT_ID_IS_ONE = 1L;
    private static final long EVENT_ID_IS_TWO = 2L;
    private static final long USER_ID_IS_ONE = 1L;
    private static final long USER_ID_IS_TWO = 2L;

    private static final int ONE_FINAL_EVENTS = 1;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    @Test
    @DisplayName("Check that one event was deleted and one remains")
    void whenListParticipantsSizeIsTwoThenSuccess() {
        Event event = Event.builder()
                .id(EVENT_ID_IS_ONE)
                .build();

        Event event1 = Event.builder()
                .id(EVENT_ID_IS_TWO)
                .build();

        ArrayList<Event> events = new ArrayList<>();
        events.add(event);
        events.add(event1);

        User user1 = User.builder()
                .id(USER_ID_IS_ONE)
                .participatedEvents(events)
                .build();

        User user2 = User.builder()
                .id(USER_ID_IS_TWO)
                .participatedEvents(events)
                .build();

        List<User> participants = List.of(user1, user2);

        event.setAttendees(participants);

        eventParticipationService.deleteParticipantsFromEvent(event);

        assertEquals(ONE_FINAL_EVENTS, user1.getParticipatedEvents().size());
        assertEquals(ONE_FINAL_EVENTS, user2.getParticipatedEvents().size());
    }
}