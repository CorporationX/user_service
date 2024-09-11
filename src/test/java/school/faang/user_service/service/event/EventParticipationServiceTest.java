package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService eventParticipationService;
    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private final static long EVENT_ID_IS_ONE = 1L;
    private final static long USER_ID_IS_ONE = 1L;
    private final static long USER_ID_IS_TWO = 2L;

    private final static int TWO_TIMES_REPOSITORY_CALLED = 2;

    @Nested
    class DeleteParticipantsFromEventMethod {

        @Test
        @DisplayName("Если findAllParticipantsByEventId возвращает список из 2х элементов," +
                "то проверяем что findAllParticipantsByEventId вызывается 1 раз и" +
                "unregister вызывается 2 раза")
        void whenListParticipantsSizeIsTwoThenSuccess() {
            Event event = Event.builder()
                    .id(EVENT_ID_IS_ONE)
                    .build();

            List<User> participants = List.of(
                    User.builder()
                            .id(USER_ID_IS_ONE)
                            .build(),
                    User.builder()
                            .id(USER_ID_IS_TWO)
                            .build());

            when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID_IS_ONE))
                    .thenReturn(participants);

            eventParticipationService.deleteParticipantsFromEvent(event);

            verify(eventParticipationRepository)
                    .findAllParticipantsByEventId(EVENT_ID_IS_ONE);
            verify(eventParticipationRepository, times(TWO_TIMES_REPOSITORY_CALLED))
                    .unregister(eq(EVENT_ID_IS_ONE), anyLong());
        }

        @Test
        @DisplayName("Если findAllParticipantsByEventId возвращает пустой список, " +
                "проверяем что метод unregister не вызывается ни разу")
        void whenNoParticipantsThenUnregisterShouldNotBeCalled() {
            Event event = Event.builder()
                    .id(EVENT_ID_IS_ONE)
                    .build();

            when(eventParticipationRepository.findAllParticipantsByEventId(EVENT_ID_IS_ONE))
                    .thenReturn(Collections.emptyList());

            eventParticipationService.deleteParticipantsFromEvent(event);
            verify(eventParticipationRepository)
                    .findAllParticipantsByEventId(EVENT_ID_IS_ONE);

            verify(eventParticipationRepository, never())
                    .unregister(anyLong(), anyLong());
        }
    }
}