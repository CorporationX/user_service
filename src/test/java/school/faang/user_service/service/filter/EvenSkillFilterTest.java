package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

public class EvenSkillFilterTest {
    private static EventSkillFilter skillFilter;
    private static EventFilterDto filterDto;
    private static List<Event> eventList;
    private static Event event;

    @BeforeAll
    public static void init() {
        skillFilter = new EventSkillFilter();
        filterDto = createEventFilterDto("test1");

        event = createEvent("test1");

        eventList = List.of(event,
                createEvent("test2"),
                createEvent("test3"),
                event);

    }

    private static EventFilterDto createEventFilterDto(String text) {
        return EventFilterDto.builder().skillPattern(text).build();
    }

    private static Event createEvent(String text) {
        return Event.builder().relatedSkills(
                List.of(Skill.builder().title(text).build())
        ).build();
    }

    @Test
    public void checkApplySizeOverZero() {
        List<Event> eventStream = Stream.of(event, event).toList();

        Assertions.assertEquals(eventStream, skillFilter.apply(eventList, filterDto).toList());
    }

    @Test
    public void checkApplySizeZero() {
        Assertions.assertEquals(List.of(), skillFilter.apply(eventList, createEventFilterDto("test12")).toList());
    }
}
