package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class EventSkillsFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getSkillsId() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        Set<Long> filterSkills = new HashSet<>(eventFilterDto.getSkillsId());
        return events.filter(event ->
                event.getRelatedSkills()
                        .stream()
                        .map(Skill::getId)
                        .allMatch(filterSkills::contains)
        );
    }
}