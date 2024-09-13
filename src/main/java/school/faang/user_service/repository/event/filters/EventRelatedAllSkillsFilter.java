package school.faang.user_service.repository.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

@Component
public class EventRelatedAllSkillsFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getRelatedAllSkillsPattern() != null &&
                !filter.getRelatedAllSkillsPattern().isBlank();
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getRelatedSkills()
                .stream()
                .allMatch(skill -> matchedSkills(filter, skill));
    }

    private boolean matchedSkills(EventFilterDto filter, Skill skill) {
        return skill.getTitle().toLowerCase()
                .contains(filter.getRelatedAllSkillsPattern().toLowerCase());
    }
}
