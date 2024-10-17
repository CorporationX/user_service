package school.faang.user_service.filter.event;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.EventFilterDto;
import school.faang.user_service.model.entity.Event;

import java.util.stream.Stream;

@Component
public class EventMaxAttendeesFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getMaxAttendees() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        return events.filter(g -> g.getMaxAttendees() >= filters.getMaxAttendees());
    }

    @Override
    public Specification<Event> toSpecification(EventFilterDto filters) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("maxAttendees"),
                        filters.getMaxAttendees()
                );
    }
}
