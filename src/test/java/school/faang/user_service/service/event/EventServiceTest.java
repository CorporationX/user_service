package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;
    @Spy
    private EventMapperImpl eventMapper;

    @Test
    void testUpdateEvent() {
        EventDto eventDto = EventDto.builder().id(1L).ownerId(1L).build();
        Event event = Event.builder().id(1L).location("location").build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        verify(eventMapper, times(1)).update(event, eventDto);
    }
    @Test
    void testDeleteEventException() {
        EventDto eventDto = EventDto.builder().id(1L).ownerId(1L).build();
        assertThrows(DataValidationException.class, () -> {
            eventService.updateEvent(eventDto);
        });
    }
}