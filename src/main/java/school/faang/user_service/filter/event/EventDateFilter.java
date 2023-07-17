package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;
import java.util.stream.Stream;

@Component
public class EventDateFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getLaterThanStartDate() != null && filter.getEarlierThanEndDate() != null;
    }

    @Override
    public void apply(List<EventDto> eventDtos, EventFilterDto filter) {
        eventDtos.removeIf(eventDto ->
                eventDto.getStartDate() == null
                        && eventDto.getEndDate() == null
                        || eventDto.getStartDate().isBefore(filter.getEarlierThanEndDate())
                        && eventDto.getEndDate().isAfter(filter.getEarlierThanEndDate()));
    }
}
