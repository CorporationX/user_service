package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RelatedSkillFilter implements Filter<Event, EventFilterDto> {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getRelatedSkillIds() != null && !filter.getRelatedSkillIds().isEmpty();
    }

    @Override
    public Stream<Event> applyFilter(Stream<Event> events, EventFilterDto filterDto) {
        return events.filter(
                event -> {
                    var eventSkillIds = event.getRelatedSkills().stream()
                            .map(Skill::getId)
                            .collect(Collectors.toSet());
                    return eventSkillIds.containsAll(filterDto.getRelatedSkillIds());
                }
        );
    }
}
