package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    @DisplayName("Получение прошедших событий")
    public void testGetPastEvents() {
        when(eventRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(List.of());

        List<Event> result = eventService.getPastEvents();

        assertTrue(result.isEmpty());

        verify(eventRepository).findAllByEndDateBefore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Удаление событий по идентификаторам")
    public void testDeleteEventsByIds() {
        doNothing().when(eventRepository).deleteAllById(List.of(1L, 2L, 3L));

        eventService.deleteEventsByIds(List.of(1L, 2L, 3L));

        verify(eventRepository).deleteAllById(List.of(1L, 2L, 3L));
    }
}
