package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceUtilsTest {
    @InjectMocks
    private EventServiceUtils utils;

    @Mock
    private SkillService skillService;

    @Mock
    private List<EventFilter> eventFilters;

    @Test
    public void testCheckOwnerHasRelatedSkillsHasAllSkills() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillsIds(List.of(1L, 2L, 3L));
        User user = User.builder().id(1L).build();
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Skill skill3 = Skill.builder().id(3L).build();
        user.setSkills(List.of(skill1, skill2, skill3));
        when(skillService.findAllByUserId(eventDto.getOwnerId())).thenReturn(user.getSkills());

        assertDoesNotThrow(() -> utils.checkOwnerHasRelatedSkills(eventDto));
    }

    @Test
    public void testCheckOwnerHasRelatedSkillsHasSomeSkills() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillsIds(List.of(1L, 2L, 3L));
        User user = User.builder().id(1L).build();
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        user.setSkills(List.of(skill1, skill2));
        when(skillService.findAllByUserId(eventDto.getOwnerId())).thenReturn(user.getSkills());

        assertThrows(DataValidationException.class, () -> utils.checkOwnerHasRelatedSkills(eventDto));
    }

    @Test
    public void testCheckOwnerHasRelatedSkillsHasNoSkills() {
        EventDto eventDto = new EventDto();
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillsIds(List.of(1L, 2L, 3L));
        User user = User.builder().id(1L).build();
        user.setSkills(List.of());
        when(skillService.findAllByUserId(eventDto.getOwnerId())).thenReturn(user.getSkills());

        assertThrows(DataValidationException.class, () -> utils.checkOwnerHasRelatedSkills(eventDto));
    }

    @Test
    public void testFilterEventsWithSomeFilters() {
        EventFilter filter1 = mock(EventFilter.class);
        EventFilter filter2 = mock(EventFilter.class);
        utils = new EventServiceUtils(null, List.of(filter1, filter2));
        EventFilterDto dto = new EventFilterDto();
        Event event = new Event();
        Stream<Event> input = Stream.of(event);

        when(filter1.isApplicable(dto)).thenReturn(true);
        when(filter1.apply(any(), eq(dto))).thenReturn(Stream.of(event));
        when(filter2.isApplicable(dto)).thenReturn(false);

        List<Event> result = utils.filterEvents(input, dto).toList();

        assertEquals(1, result.size());
        assertEquals(event, result.get(0));
        verify(filter1).apply(any(), eq(dto));
        verify(filter2, never()).apply(any(), any());
    }

    @Test
    public void testFilterEventsWithOneFilter() {
        EventFilter filter1 = mock(EventFilter.class);
        EventFilter filter2 = mock(EventFilter.class);
        utils = new EventServiceUtils(null, List.of(filter1, filter2));
        EventFilterDto dto = new EventFilterDto();
        Event event = new Event();
        Stream<Event> input = Stream.of(event);

        when(filter1.isApplicable(dto)).thenReturn(false);
        when(filter2.isApplicable(dto)).thenReturn(false);

        List<Event> result = utils.filterEvents(input, dto).toList();

        assertEquals(1, result.size()); // поток не отфильтровался
        assertEquals(event, result.get(0));
        verify(filter1, never()).apply(any(), any());
        verify(filter2, never()).apply(any(), any());
    }

    @Test
    public void testFilterEventsWithNoFilters() {
        EventFilter filter1 = mock(EventFilter.class);
        EventFilter filter2 = mock(EventFilter.class);
        utils = new EventServiceUtils(null, List.of(filter1, filter2));
        EventFilterDto dto = new EventFilterDto();
        Event filteredEvent = new Event();
        Event event = new Event();
        Stream<Event> input = Stream.of(event);
        when(filter1.isApplicable(dto)).thenReturn(true);
        when(filter1.apply(any(), eq(dto))).thenReturn(Stream.of(filteredEvent));
        when(filter2.isApplicable(dto)).thenReturn(true);
        when(filter2.apply(any(), eq(dto))).thenReturn(Stream.of(filteredEvent)); // второй фильтр тоже пропускает

        List<Event> result = utils.filterEvents(input, dto).toList();

        assertEquals(1, result.size());
        assertEquals(filteredEvent, result.get(0));
        verify(filter1).apply(any(), eq(dto));
        verify(filter2).apply(any(), eq(dto));
    }
}