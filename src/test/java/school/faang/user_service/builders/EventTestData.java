package school.faang.user_service.builders;

import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

public class EventTestData {
    private Long id = 1L;
    private String title = "Default Title";
    private String description = "Default Description";
    private LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    private LocalDateTime endDate = LocalDateTime.now().plusDays(1);
    private int maxAttendees = 5;
    private EventType type = EventType.WEBINAR;
    private EventStatus status = EventStatus.PLANNED;

    public static EventTestData defaultEvent() {
        return new EventTestData();
    }

    public EventTestData withId(Long id) {
        this.id = id;
        return this;
    }

    public EventTestData withTitle(String title) {
        this.title = title;
        return this;
    }

    public EventTestData withStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public EventTestData withEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public EventTestData withMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
        return this;
    }

    public EventTestData withType(EventType type) {
        this.type = type;
        return this;
    }

    public EventTestData withStatus(EventStatus status) {
        this.status = status;
        return this;
    }

    public Event build() {
        return Event.builder()
                .id(id)
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .type(type)
                .status(status)
                .maxAttendees(maxAttendees)
                .build();
    }
}
