package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidatorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventValidatorImpl validator;
    @Mock
    private EventFilterService filterService;
    @Mock
    private EventMapper mapper;
    @InjectMocks
    private EventServiceImpl service;

    private final long id = 1L;
    private Event event1;
    private Event event2;
    private EventDto eventDto1;
    private EventDto eventDto2;

    @BeforeEach
    void init() {
        event1 = Event.builder()
                .id(1L)
                .build();
        event2 = Event.builder()
                .id(2L)
                .build();
        eventDto1 = EventDto.builder()
                .id(1L)
                .build();
        eventDto2 = EventDto.builder()
                .id(2L)
                .build();
    }

    @Test
    void getParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(id)).thenReturn(List.of(event1, event2));
        when(mapper.toDto(event1)).thenReturn(eventDto1);
        when(mapper.toDto(event2)).thenReturn(eventDto2);

        List<EventDto> output = service.getParticipatedEvents(id);
        assertArrayEquals(new EventDto[]{eventDto1, eventDto2}, output.toArray());

        InOrder inOrder = inOrder(eventRepository, mapper);
        inOrder.verify(eventRepository, times(1)).findParticipatedEventsByUserId(id);
        inOrder.verify(mapper, times(1)).toDto(event1);
        inOrder.verify(mapper, times(1)).toDto(event2);
    }

    @Test
    void getOwnedEvents() {
        when(eventRepository.findAllByUserId(id)).thenReturn(List.of(event1, event2));
        when(mapper.toDto(event1)).thenReturn(eventDto1);
        when(mapper.toDto(event2)).thenReturn(eventDto2);

        List<EventDto> output = service.getOwnedEvents(id);
        assertArrayEquals(new EventDto[]{eventDto1, eventDto2}, output.toArray());

        InOrder inOrder = inOrder(eventRepository, mapper);
        inOrder.verify(eventRepository, times(1)).findAllByUserId(id);
        inOrder.verify(mapper, times(1)).toDto(event1);
        inOrder.verify(mapper, times(1)).toDto(event2);
    }

    @Test
    void updateEvent() {
        eventDto1.setOwnerId(id);
        eventDto1.setRelatedSkills(new ArrayList<>());

        when(eventRepository.save(event1)).thenReturn(event1);
        when(mapper.toEntity(eventDto1)).thenReturn(event1);
        when(mapper.toDto(event1)).thenReturn(eventDto1);

        EventDto output = service.updateEvent(eventDto1);
        assertEquals(eventDto1, output);

        InOrder inOrder = inOrder(eventRepository, validator, mapper);
        inOrder.verify(validator, times(1)).validate(eventDto1);
        inOrder.verify(validator, times(1)).validateOwnersRequiredSkills(eventDto1);
        inOrder.verify(mapper, times(1)).toEntity(eventDto1);
        inOrder.verify(eventRepository, times(1)).save(event1);
        inOrder.verify(mapper, times(1)).toDto(event1);
    }

    @Test
    void deleteEvent() {
        when(eventRepository.findById(id)).thenReturn(Optional.of(event1));
        when(mapper.toDto(event1)).thenReturn(eventDto1);

        EventDto output = service.deleteEvent(id);
        assertEquals(eventDto1, output);

        InOrder inOrder = inOrder(eventRepository, mapper);
        inOrder.verify(eventRepository, times(1)).findById(id);
        inOrder.verify(eventRepository, times(1)).deleteById(id);
        inOrder.verify(mapper, times(1)).toDto(event1);
    }

    @Test
    void getEventsByFilter() {
        List<Event> events = List.of(event1, event2);
        Stream<Event> eventStream = events.stream();
        EventFilterDto filterDto = EventFilterDto.builder().build();

        when(eventRepository.findAll()).thenReturn(events);
        when(filterService.apply(any(), eq(filterDto))).thenReturn(eventStream);
        when(mapper.toDto(event1)).thenReturn(eventDto1);
        when(mapper.toDto(event2)).thenReturn(eventDto2);

        List<EventDto> output = service.getEventsByFilter(filterDto);
        assertArrayEquals(new EventDto[]{eventDto1, eventDto2}, output.toArray());

        InOrder inOrder = inOrder(eventRepository, filterService, mapper);
        inOrder.verify(eventRepository, times(1)).findAll();
        inOrder.verify(filterService, times(1)).apply(any(), eq(filterDto));
        inOrder.verify(mapper, times(1)).toDto(event1);
        inOrder.verify(mapper, times(1)).toDto(event2);
    }

    @Test
    void getEvent() {
        when(eventRepository.findById(id)).thenReturn(Optional.of(event1));
        when(mapper.toDto(event1)).thenReturn(eventDto1);

        EventDto output = service.getEvent(id);
        assertEquals(eventDto1, output);

        InOrder inOrder = inOrder(eventRepository, mapper);
        inOrder.verify(eventRepository, times(1)).findById(id);
        inOrder.verify(mapper, times(1)).toDto(event1);
    }

    @Test
    void create() {
        eventDto1.setOwnerId(id);
        eventDto1.setRelatedSkills(new ArrayList<>());

        when(eventRepository.save(event1)).thenReturn(event1);
        when(mapper.toEntity(eventDto1)).thenReturn(event1);
        when(mapper.toDto(event1)).thenReturn(eventDto1);

        EventDto output = service.create(eventDto1);
        assertEquals(eventDto1, output);

        InOrder inOrder = inOrder(eventRepository, validator, mapper);
        inOrder.verify(validator, times(1)).validate(eventDto1);
        inOrder.verify(validator, times(1)).validateOwnersRequiredSkills(eventDto1);
        inOrder.verify(mapper, times(1)).toEntity(eventDto1);
        inOrder.verify(eventRepository, times(1)).save(event1);
        inOrder.verify(mapper, times(1)).toDto(event1);
    }
}