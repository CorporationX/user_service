package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;

import java.util.stream.Stream;

@Component
public class EventRateFilter implements EventFilter {
    private static final double DEFAULT_RATE = 0.0;

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getAverageRate() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events.filter(event ->
                event.getRatings()
                        .stream()
                        .mapToLong(Rating::getRate)
                        .average()
                        .orElse(DEFAULT_RATE) >= eventFilterDto.getAverageRate());
    }
}
