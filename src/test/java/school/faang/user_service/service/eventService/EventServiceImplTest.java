package school.faang.user_service.service.eventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventServiceImpl eventService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

    }

    @Test
    @DisplayName("Remove events by user id")
    void testDeleteEventByUserId() {
        Event event = new Event();
        event.setId(1L);
        event.setOwner(user);
        Event event2 = new Event();
        event2.setId(2L);
        event2.setOwner(user);
        when(eventRepository.findAllByUserId(1L)).thenReturn(new ArrayList<>(List.of(event, event2)));
        eventService.deleteEventByUserId(1L);

        verify(eventRepository).deleteAllById(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Remove events by user id with no events")
    void testDeleteEventByUserId_NoEvents() {
        when(eventRepository.findAllByUserId(1L)).thenReturn(new ArrayList<>());
        eventService.deleteEventByUserId(1L);

        verify(eventRepository).deleteAllById(List.of());
    }

}