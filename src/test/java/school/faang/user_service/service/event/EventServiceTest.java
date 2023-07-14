package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;

    @Test
    public void testGetEventThrowEntityNotFoundException() {
        Mockito.when(eventRepository.findById(1L)).thenThrow(new EntityNotFoundException("Event not found"));
        assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(1L));
    }

    @Test
    public void testGetEvent() {
        EventDto eventDto = new EventDto(1L, null, null, null, null, null, null, null, 1);
        Event event = Event.builder().id(1L).maxAttendees(1).build();
        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.ofNullable(event));
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDto);
        assertEquals(eventDto, eventService.getEvent(1L));
    }

}