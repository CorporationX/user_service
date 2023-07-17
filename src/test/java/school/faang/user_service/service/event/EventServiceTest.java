package school.faang.user_service.service.event;


import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    EventDto eventDto;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventValidator eventValidator;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;

    @BeforeEach
    public void init() {
        eventDto = new EventDto(4L, "fdgdfg", LocalDateTime.now(), LocalDateTime.now(),
                0L, "hfgh", new ArrayList<>(), "location", 1);

    }

    @Test
    public void testCreateEvent() {
        eventService.createEvent(eventDto);
        Mockito.verify(eventRepository, Mockito.times(1)).save(eventMapper.toEntity(eventDto));
    }

    @Test
    public void testSkillsValidation() {
        eventValidator.checkIfUserHasSkillsRequired(eventDto);
        Mockito.verify(eventValidator, Mockito.times(1)).checkIfUserHasSkillsRequired(eventDto);
    }

    @Test
    public void testDeleteEvent() {
        long id = 3L;
        var event = Event.builder().id(id).build();
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        eventService.deleteEvent(id);
        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(id);
    }

    public void testCreateEventWithMapper() {
        Event event = Event.builder().id(4L).maxAttendees(1).build();
        EventDto eventDto1 = new EventDto(4L, null,null,null,null,null,null,null,1);
        Mockito.when(eventMapper.toEntity(eventDto1)).thenReturn(event);
        Mockito.when(eventRepository.save(event)).thenReturn(event);
        Mockito.when(eventMapper.toDto(event)).thenReturn(eventDto1);
        Assertions.assertEquals(eventDto1, eventService.createEvent(eventDto1));
    }
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