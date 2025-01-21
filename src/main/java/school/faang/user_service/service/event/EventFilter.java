package school.faang.user_service.service.event;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Predicate;
import school.faang.user_service.entity.event.Event;

public class EventFilter implements Predicate<Event> {
    private final EventFilterDto filter;

    public EventFilter(EventFilterDto filter) {
        this.filter = filter;
    }

    @Override
    public boolean test(Event event) {
        return (filter.getTitle() == null || event.getTitle().contains(filter.getTitle())) &&
                (filter.getOwnerId() == null || event.getOwner().getId().equals(filter.getOwnerId())) &&
                (filter.getSkillId() == null || event.getRelatedSkills().stream().anyMatch(skill -> skill.getId() == filter.getSkillId())) &&
                (filter.getStartDate() == null || event.getStartDate().isAfter(LocalDateTime.ofEpochSecond(filter.getStartDate(), 0, ZoneOffset.UTC))) &&
                (filter.getEndDate() == null || event.getEndDate().isBefore(LocalDateTime.ofEpochSecond(filter.getEndDate(), 0, ZoneOffset.UTC))) &&
                (filter.getMinAttendees() == null || event.getAttendees().size() >= filter.getMinAttendees()) &&
                (filter.getMaxAttendees() == null || event.getAttendees().size() <= filter.getMaxAttendees());
    }
}