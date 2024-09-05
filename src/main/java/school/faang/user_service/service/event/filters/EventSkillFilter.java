package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

@Component
public class EventSkillFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getSkillPattern() != null &&
                !eventFilterDto.getSkillPattern().isBlank();
    }

    @Override
    public Stream<Event> apply(List<Event> eventList, EventFilterDto eventFilterDto) {
        return eventList.stream()
                .filter(event -> skillPatternFilter(eventFilterDto, event));
    }

    private static boolean skillPatternFilter(EventFilterDto eventFilterDto, Event event) {
        var matchedSkillsList = event.getRelatedSkills().stream()
                .map(Skill::getTitle)
                .filter(skill -> skill.contains(eventFilterDto.getSkillPattern()))
                .toList();

        return matchedSkillsList.size() > 0;
    }
}
