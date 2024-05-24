package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

@Component
class EventSkillFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getSkillPattern() != null && !filters.getSkillPattern().isBlank();
    }

    @Override
    public Stream<Event> apply(List<Event> events, EventFilterDto filters) {
        return events.stream()
                .filter(event -> {
                    var matchedSkillsList = event.getRelatedSkills().stream()
                            .map(Skill::getTitle)
                            .filter(skill -> skill.contains(filters.getSkillPattern()))
                            .toList();

                    return matchedSkillsList.size() > 0;
                });
    }
}
