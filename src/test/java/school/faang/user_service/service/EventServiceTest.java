package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventParticipationRepository eventParticipationRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;

    @Test
    void deleteAllParticipatedEventsByUserId() {
        long userId = 1;
        eventService.deleteAllParticipatedEventsByUserId(userId);
        verify(eventParticipationRepository, times(1))
                .deleteAllParticipatedEventsByUserId(userId);

    }

    @Test
    void deleteALLEventByUserId() {
        long userId = 1;
        eventService.deleteALLEventByUserId(userId);
        verify(eventRepository,times(1)).deleteAllByUserId(userId);
    }
}