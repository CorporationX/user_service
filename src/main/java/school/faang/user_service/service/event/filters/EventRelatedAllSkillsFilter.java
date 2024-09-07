package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

@Component
public class EventRelatedAllSkillsFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getRelatedAllSkillsPattern() != null;
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getRelatedSkills()
                .stream()
                .allMatch(relatedSkill -> matchedSkills(filter, relatedSkill));
    }

    private boolean matchedSkills(EventFilterDto filter, Skill relatedSkill) {
        return filter.getRelatedAllSkillsPattern()
                .stream()
                .allMatch(skill -> skill.getTitle().toLowerCase()
                        .contains(relatedSkill.getTitle().toLowerCase())
                );
    }
}
