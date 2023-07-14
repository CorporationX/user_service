package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setStartDate(LocalDateTime.now());
        event.setEndDate(LocalDateTime.now().plusHours(2));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventDto eventDto = eventService.getEvent(1L);

        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getTitle(), eventDto.getTitle());
        assertEquals(event.getStartDate(), eventDto.getStartDate());
        assertEquals(event.getEndDate(), eventDto.getEndDate());
    }

}