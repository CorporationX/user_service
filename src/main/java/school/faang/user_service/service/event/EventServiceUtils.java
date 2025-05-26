package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class EventServiceUtils {
    private final SkillService skillService;
    private final List<EventFilter> eventFilters;

    public void checkOwnerHasRelatedSkills(EventDto eventDto) {
        Optional.of(
                        skillService.findAllByUserId(eventDto.getOwnerId()).stream()
                                .map(Skill::getId)
                                .collect(toSet())
                )
                .filter(ids -> ids.containsAll(eventDto.getRelatedSkillsIds()))
                .orElseThrow(() -> new DataValidationException("Owner doesn't have all related skills"));
    }

    public Stream<Event> filterEvents(Stream<Event> events, EventFilterDto eventFilterDto) {
        return eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilterDto))
                .reduce(events,
                        (streamEvents, filter) ->
                                filter.apply(streamEvents, eventFilterDto),
                        (Stream::concat));
    }
}
