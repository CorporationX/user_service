package school.faang.user_service.filter.event;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.EventFilterDto;
import school.faang.user_service.model.entity.Event;

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
