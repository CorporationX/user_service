package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.validation.Validator;

import java.util.List;
import java.util.stream.Stream;

@Component
class EventDescriptionFilter implements EventFilter {
    private Validator validator;

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return validator.checkStringIsNullAndEmpty(filters.getDescriptionPattern());
    }

    @Override
    public Stream<Event> apply(List<Event> events, EventFilterDto filters) {
        return events.stream()
                .filter(event -> event.getDescription().contains(filters.getDescriptionPattern()));
    }
}