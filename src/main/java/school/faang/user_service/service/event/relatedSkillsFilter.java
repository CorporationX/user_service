package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class relatedSkillsFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return !eventFilterDto.getRelatedSkillIds().isEmpty();
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
//        events
//                .filter(event -> {
//                    event.getRelatedSkills().stream()
//                            .map(Skill::getId)
//                            .toList();
//                })
        return null;
    }
}
