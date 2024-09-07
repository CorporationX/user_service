package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

@Component
public class EventRelatedAnySkillsFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getRelatedAnySkillsPattern() != null;
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getRelatedSkills()
                .stream()
                .anyMatch(skillEvent -> matchedSkills(filter, skillEvent));
    }

    private boolean matchedSkills(EventFilterDto eventFilterDto, Skill skill) {
        return eventFilterDto.getRelatedAnySkillsPattern()
                .stream()
                .anyMatch(skillFilter -> skill.getTitle().toLowerCase()
                        .contains(skillFilter.getTitle().toLowerCase())
                );
    }
}
