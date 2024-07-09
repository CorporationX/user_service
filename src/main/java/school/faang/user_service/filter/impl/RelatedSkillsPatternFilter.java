package school.faang.user_service.filter.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;

import java.util.List;
import java.util.stream.Stream;

@Component
public class RelatedSkillsPatternFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getRelatedSkillIsPattern() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filters) {
        // Выбирает событие, в которых есть все skills и не более
        if (filters.isOptionFilterSkills()) {
            return containsAllSkills(eventStream, filters.getRelatedSkillIsPattern());

            // Выбирает события, в которых есть хотя бы один из указанных умений
        } else {
            return containsAnySkills(eventStream, filters.getRelatedSkillIsPattern());
        }
    }

    @Override
    public boolean test(Event event, EventFilterDto filters) {
        // Выбирает событие, в которых есть все skills и не более
        if (filters.isOptionFilterSkills()) {
            return event.getRelatedSkills().stream()
                    .allMatch(skillEvent -> filters.getRelatedSkillIsPattern().stream()
                            .anyMatch(skillFilter -> skillEvent.getTitle().toLowerCase().contains(skillFilter.getTitle().toLowerCase()))
                    );
            // Выбирает события, в которых есть хотя бы один из указанных умений
        } else {
            return event.getRelatedSkills().stream()
                    .anyMatch(skillEvent -> filters.getRelatedSkillIsPattern().stream()
                            .anyMatch(skillFilter -> skillEvent.getTitle().toLowerCase().contains(skillFilter.getTitle().toLowerCase()))
                    );
        }
    }

    private Stream<Event> containsAllSkills(Stream<Event> eventStream, List<Skill> skillsFilter) {
        return eventStream.filter(event -> event.getRelatedSkills().stream()
                .allMatch(skillEvent -> skillsFilter.stream()
                        .anyMatch(skillFilter -> skillEvent.getTitle().toLowerCase().contains(skillFilter.getTitle().toLowerCase()))
                )
        );
    }

    private Stream<Event> containsAnySkills(Stream<Event> eventStream, List<Skill> skillsFilter) {
        return eventStream.filter(event -> event.getRelatedSkills().stream()
                .anyMatch(skillEvent -> skillsFilter.stream()
                        .anyMatch(skillFilter -> skillEvent.getTitle().toLowerCase().contains(skillFilter.getTitle().toLowerCase()))
                )
        );
    }

}
