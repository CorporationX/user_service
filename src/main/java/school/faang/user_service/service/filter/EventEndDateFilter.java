package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.validation.Validator;

import java.util.List;
import java.util.stream.Stream;

@Component
class EventEndDateFilter implements EventFilter {
    private Validator validator;

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return validator.checkLocalDateTimeIsNull(filters.getEndDatePattern());
    }

    @Override
    public Stream<Event> apply(List<Event> events, EventFilterDto filters) {
        return events.stream()
                .filter(event -> event.getEndDate().isBefore(filters.getEndDatePattern())
                        || event.getEndDate().equals(filters.getEndDatePattern()));
    }
}