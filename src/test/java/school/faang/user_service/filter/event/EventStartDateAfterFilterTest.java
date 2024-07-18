package school.faang.user_service.filter.event;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

class EventStartDateAfterFilterTest {

    private final LocalDateTime DATE_NOW = LocalDateTime.now();

    @Test
    void apply() {
        EventFilterDto eventFilterDto = new EventFilterDto("fake", DATE_NOW.minusDays(1));
        Event expectedEvent = getEvent();
        Event eventIsFalse = getEvent();
        eventIsFalse.setStartDate(DATE_NOW.minusDays(2));

        List<Event> actualEvents = new EventStartDateAfterFieldFilter()
                .apply(List.of(expectedEvent, eventIsFalse).stream(), eventFilterDto)
                .toList();

        Assertions.assertThat(actualEvents).hasSize(1);
        Event actualEvent = actualEvents.iterator().next();
        Assertions.assertThat(actualEvent).isEqualTo(expectedEvent);
    }

    private Event getEvent() {
        return Event.builder()
                .title("title")
                .startDate(DATE_NOW)
                .endDate(DATE_NOW.plusDays(1))
                .owner(getUser(1L))
                .description("description")
                .relatedSkills(Stream.of(1L, 2L)
                        .map(this::getSkill)
                        .toList())
                .type(EventType.POLL)
                .status(EventStatus.PLANNED)
                .maxAttendees(1)
                .build();
    }

    private Skill getSkill(Long id) {
        return Skill.builder()
                .id(id)
                .build();
    }

    private User getUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }
}