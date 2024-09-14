package school.faang.user_service.filter.event;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventTitleFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getTitlePattern() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> users, EventFilterDto filters) {
        return users.filter(g -> g.getTitle().contains(filters.getTitlePattern()));
    }

    @Override
    public Specification<Event> toSpecification(EventFilterDto filters) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + filters.getTitlePattern().toLowerCase() + "%"
                );
    }
}
