package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

@Component
public class EventRelatedAnySkillsFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getRelatedAnySkillsPattern() != null &&
                !filter.getRelatedAnySkillsPattern().isBlank();
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getRelatedSkills()
                .stream()
                .anyMatch(skill -> matchedSkills(filter, skill));
    }

    private boolean matchedSkills(EventFilterDto filter, Skill skill) {
        return skill.getTitle().toLowerCase()
                .contains(filter.getRelatedAnySkillsPattern().toLowerCase());
    }
}
