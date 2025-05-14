package school.faang.user_service.filter.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EventSkillsFilterTest {

    @InjectMocks
    private EventSkillsFilter filter;

    @Test
    public void testIsApplicableTrue() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setSkillsId(List.of(1L, 2L));
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    public void testIApplicabelFalse() {
        EventFilterDto filterDto = new EventFilterDto();
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    public void testApply() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setSkillsId(List.of(1L, 2L));
        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Skill skill3 = Skill.builder().id(3L).build();
        Event event1 = Event.builder().id(1L).relatedSkills(List.of(skill1, skill3)).build();
        Event event2 = Event.builder().id(2L).relatedSkills(List.of(skill2)).build();
        Event event3 = Event.builder().id(3L).relatedSkills(List.of(skill1, skill2)).build();
        Event event4 = Event.builder().id(4L).relatedSkills(List.of(skill1, skill2, skill3)).build();

        List<Event> result = filter.apply(List.of(event1, event2, event3, event4).stream(), filterDto).toList();

        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(4L, result.get(1).getId());
    }
}