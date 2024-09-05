package school.faang.user_service.service.event.util;

import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class EventUpdater {

    private Event event;
    public EventUpdater withTitle(String title) {
        event.setTitle(title);
        return this;
    }

    public EventUpdater withDescription(String description) {
        event.setDescription(description);
        return this;
    }

    public EventUpdater withStartDate(LocalDateTime startDate) {
        event.setStartDate(startDate);
        return this;
    }

    public EventUpdater withEndDate(LocalDateTime endDate) {
        event.setEndDate(endDate);
        return this;
    }

    public EventUpdater withLocation(String location) {
        event.setLocation(location);
        return this;
    }

    public EventUpdater withMaxAttendees(int maxAttendees) {
        event.setMaxAttendees(maxAttendees);
        return this;
    }

    public EventUpdater withRelatedSkills(List<Skill> relatedSkills) {
        event.setRelatedSkills(relatedSkills);
        return this;
    }

    public EventUpdater withOwner(User owner) {
        event.setOwner(owner);
        return this;
    }
    public EventUpdater withEventType(EventType eventType) {
        event.setType(eventType);
        return this;
    }
    public EventUpdater withEventStatus(EventStatus eventStatus) {
        event.setStatus(eventStatus);
        return this;
    }

    public Event build() {
        return event;
    }
}
