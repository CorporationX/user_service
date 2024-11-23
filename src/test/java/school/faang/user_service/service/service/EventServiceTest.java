package school.faang.user_service.service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
    @Mock
    private List<EventFilter> eventFilters;
    @Mock
    EventFilterDto eventFilterDto;
    @Captor
    private ArgumentCaptor<Long> argumentCaptor;
    @Captor
    private ArgumentCaptor<EventFilterDto> eventFilterCaptor;
    @Captor
    private ArgumentCaptor<EventDto> eventDtoCaptor;
    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    EventDto eventDto;
    List<EventDto> eventDtos;
    long eventId = 5;
    long userId = 4;

    Event event;

    @BeforeEach
    public void setupEvent() {
        event = new Event();
        event.setId(2);
        event.setTitle("EventTitle");
        event.setDescription("eventDescription");
    }

    @BeforeEach
    public void setupEventDto() {
        eventDto = new EventDto();
        eventDto.setTitle("title");
        eventDto.setDescription("description");
        eventDto.setId(1L);
        eventDto.setOwnerId(2L);
        eventDto.setStartDate(LocalDateTime.of(2024, 11, 21, 10, 30));
    }

    @BeforeEach
    public void setupEventDtoList() {
        eventDtos = new ArrayList<>();
        EventDto firstEventDto = new EventDto();
        EventDto secondEventDto = new EventDto();
        firstEventDto.setTitle("firstTitle");
        secondEventDto.setTitle("secondTitle");
        eventDtos.add(firstEventDto);
        eventDtos.add(secondEventDto);
    }

    @Test
    public void testWithNoRequiredSkills() {
        List<Long> skillId = List.of(1L, 3L, 6L);
        when(eventDto.getRelatedSkills()
                .stream().map(SkillDto::getId).collect(Collectors.toList())).thenReturn(skillId);
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())
                .stream().map(Skill::getId).collect(Collectors.toList())).thenReturn(null);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));

    }

    @Test
    public void testCreate() {
        List<Long> skillId = List.of(1L, 3L, 6L);
        when(eventDto.getRelatedSkills()
                .stream().map(SkillDto::getId).collect(Collectors.toList())).thenReturn(skillId);
        when(skillRepository.findAllByUserId(eventDto.getOwnerId())
                .stream().map(Skill::getId).collect(Collectors.toList())).thenReturn(skillId);

        EventDto result = eventService.create(eventDto);
        verify(eventMapper, times(1)).toEntity(eventDtoCaptor.capture());
        EventDto capturedEventDto = eventDtoCaptor.getValue();
        assertEquals(eventDto, capturedEventDto);
        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();
        assertEquals(eventMapper.toEntity(eventDto), capturedEvent);
    }

    @Test
    public void testGetEventWithNoEvent() {
        when(eventRepository.findById(eventId)).thenReturn(null);
        assertThrows(DataValidationException.class, () -> eventService.getEvent(eventId));
    }

    public void testGetEventWithEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventMapper.toEntity(eventDto)));
        EventDto result = eventService.getEvent(eventId);
        verify(eventRepository, times(1)).findById(argumentCaptor.capture());
        Long capturedEventId = argumentCaptor.getValue();
        verify(eventMapper, times(1)).toDto(eventCaptor.capture());
        Event capturedEvent = eventCaptor.getValue();

        assertEquals(eventId, capturedEventId);
        assertEquals(eventMapper.toEntity(eventDto), capturedEvent);
        assertEquals(eventDto, result);
    }

    public void testGetEventsByFilter() {
        when(eventRepository.findAll()).thenReturn(eventMapper.toEntityList(eventDtos));
        Stream<Event> eventsStream = eventMapper.toEntityList(eventDtos).stream();
        when(eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(eventFilterDto))
                .reduce(eventsStream, (stream, eventFilter) -> eventFilter.apply(stream, eventFilterDto), (s1, s2) -> s1)
                .map(eventMapper::toDto).toList()).thenReturn(eventDtos);

        List<EventDto> gotEventDtoList = eventService.getEventsByFilter(eventFilterDto);
        assertEquals(eventDtos, gotEventDtoList);

    }

    @Test
    public void testDeleteEvent() {
        long result = eventService.deleteEvent(eventId);
        verify(eventRepository, times(1)).deleteById(argumentCaptor.capture());
        long capturedEventId = argumentCaptor.getValue();
        Assert.assertEquals(eventId, capturedEventId);
        Assert.assertEquals(eventId, result);

    }

    @Test
    public void testUpdateEvent() {
        when(eventRepository.save(eventMapper.toEntity(eventDto))).thenReturn(eventMapper.toEntity(eventDto));

        EventDto updatedEvent = eventService.updateEvent(eventDto);
        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();
        assertEquals(eventMapper.toEntity(eventDto), savedEvent);
        assertEquals(eventDto, updatedEvent);
    }

    @Test
    public void testGetOwnedEvents() {
        when(eventRepository.findAllByUserId(userId)).thenReturn(eventMapper.toEntityList(eventDtos));

        List<EventDto> reslutList = eventService.getOwnedEvents(userId);
        verify(eventRepository, times(1)).findAllByUserId(argumentCaptor.capture());
        long capturedUserId = argumentCaptor.getValue();
        assertEquals(userId, capturedUserId);
        assertEquals(eventDtos, reslutList);
    }

    @Test
    public void testGetParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(eventMapper.toEntityList(eventDtos));

        List<EventDto> reslutList = eventService.getParticipatedEvents(userId);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(argumentCaptor.capture());
        long capturedUserId = argumentCaptor.getValue();
        assertEquals(userId, capturedUserId);
        assertEquals(eventDtos, reslutList);
    }
}
