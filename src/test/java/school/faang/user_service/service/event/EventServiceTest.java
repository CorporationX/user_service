package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    EventServiceUtils eventServiceUtils;

    @Mock
    private SkillService skillService;

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Test
    public void testCreateEventWhenOwnerHasAllRelatedSkills() {
        EventDto dto = createEventDto(1L, null, List.of(1L, 2L));
        List<Skill> skills = createSkills(1L, 2L);
        doNothing().when(eventServiceUtils).checkOwnerHasRelatedSkills(any());
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        EventDto result = eventService.create(dto);

        assertNotNull(result);
        assertEquals(dto.getOwnerId(), result.getOwnerId());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    public void testCreateEventOwnerHasNoRelatedSkills() {
        EventDto dto = createEventDto(1L, null, List.of(1L, 2L, 99L));
        List<Skill> skills = createSkills(1L, 2L);
        doThrow(DataValidationException.class).when(eventServiceUtils).checkOwnerHasRelatedSkills(any());
        assertThrows(DataValidationException.class, () -> eventService.create(dto));
    }

    @Test
    public void testGetEventByEventIdWithoutItInDatabase() {
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.empty());

        assertThrows(DataValidationException.class, () -> eventService.getEvent(eventId));
    }

    @Test
    public void testGetEventByEventId() {
        Long eventId = 1L;
        EventDto eventDto = createEventDto(null, eventId, null);
        Event event = new Event();
        event.setId(eventId);
        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEvent(eventId);

        assertEquals(eventId, result.getId());
        verify(eventRepository).findById(eventId);
    }

    /*@Test
    public void testGetEventsWhenAllFiltersAdded() {
//        EventFilterDto filter = mock(EventFilterDto.class);
        EventFilterDto filter = new EventFilterDto();
        filter.setStartDate(LocalDateTime.of(2025, 1, 1, 0, 0));
        filter.setOwnerId(1L);
        filter.setEventType("EVENT");
        filter.setEndDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        filter.setLocation("Location");
        filter.setSkillsId(List.of(1L, 2L));
        filter.setStatus("STATUS");
        filter.setMaxAttendees(50);
        filter.setAverageRate(3L);

        Event event = new Event();
        event.setId(1L);

        EventDto expectedDto = new EventDto();
        expectedDto.setId(1L);

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toDto(event)).thenReturn(expectedDto);

        assertEquals(List.of(expectedDto), eventService.getEventsByFilter(filter));

//        List<EventFilter> filters = IntStream.range(0, 9)
//                .mapToObj(i -> {
//                    EventFilter f = mock(EventFilter.class);
//                    when(f.isApplicable(filter)).thenReturn(true);
//                    when(f.apply(any(), eq(filter))).thenAnswer(inv -> inv.getArgument(0));
//                    return f;
//                })
//                .toList();

//        ReflectionTestUtils.setField(eventService, "eventFilters", filters);
//
//        List<EventDto> result = eventService.getEventsByFilter(filter);
//
//        assertEquals(1, result.size());
//        assertEquals(1L, result.get(0).getId());
//        verify(eventRepository).findAll();
//        for (EventFilter f : filters) {
//            verify(f).isApplicable(filter);
//            verify(f).apply(any(), eq(filter));
//        }

    }*/

    @Test
    public void testGetEventsWhenNoneFiltersAdded() {
    }


    private EventDto createEventDto(Long ownerId, Long id, List<Long> skillIds) {
        EventDto dto = new EventDto();
        dto.setOwnerId(ownerId);
        dto.setId(id);
        dto.setRelatedSkillsIds(skillIds);
        return dto;
    }

    private Skill createSkill(Long id) {
        Skill skill = new Skill();
        skill.setId(id);
        return skill;
    }

    private List<Skill> createSkills(Long... ids) {
        return Arrays.stream(ids)
                .map(this::createSkill)
                .toList();
    }

}