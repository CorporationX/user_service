package school.faang.user_service.service.event.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EventRelatedSkillsFilterTest {
    private EventRelatedSkillsFilter filter;
    private EventFilterDto filterDto;
    private Event event1, event2, event3;

    @BeforeEach
    void init() {
        filter = new EventRelatedSkillsFilter();
        filterDto = new EventFilterDto();
        event1 = Event.builder().relatedSkills(List.of(Skill.builder().id(1L).build())).build();
        event2 = Event.builder().relatedSkills(List.of(Skill.builder().id(2L).build())).build();
        event3 = Event.builder().relatedSkills(List.of(Skill.builder().id(1L).build())).build();
    }

    @Test
    void isAcceptableNullDto() {
        assertFalse(filter.isAcceptable(filterDto));
    }

    @Test
    void isAcceptableEmptyDto() {
        filterDto.setRelatedSkills(new ArrayList<>());
        assertFalse(filter.isAcceptable(filterDto));
    }

    @Test
    void isAcceptableGoodDto() {
        filterDto.setRelatedSkills(List.of(SkillDto.builder().id(1L).build()));
        assertTrue(filter.isAcceptable(filterDto));
    }

    @Test
    void apply() {
        filterDto.setRelatedSkills(List.of(SkillDto.builder().id(1L).build()));
        Event[] expected = new Event[]{event1, event3};
        Stream<Event> out = filter.apply(Stream.of(event1, event2, event3), filterDto);
        assertArrayEquals(expected, out.toArray());
    }
}