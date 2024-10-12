package school.faang.user_service.service.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

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

    @Test
    public void testGetEvent() {
        List<Long> ids = List.of(1L);
        LocalDateTime date = LocalDateTime.of(2020, 1, 1, 0, 0);
        Event event = new Event();
        event.setTitle("test");
        event.setStartDate(date);
        event.setId(1L);
        EventDto eventDto = new EventDto(ids,1L, "test", date);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toEventDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(1L);

        assertEquals(eventDto.eventId(), result.eventId());
        assertEquals(eventDto.title(), result.title());
        assertEquals(eventDto.startDateTime(), result.startDateTime());
    }
}
