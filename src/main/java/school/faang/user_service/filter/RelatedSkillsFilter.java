package school.faang.user_service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.mapper.EventMapper;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RelatedSkillsFilter implements EventFilter {

    private final EventMapper eventMapper;

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getRelatedSkills() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {

        Set<String> setTitleSkillsEvent = filters.getRelatedSkills().stream()
                .map(SkillDto::getTitle)
                .collect(Collectors.toSet());

        return events.filter(filter -> setTitleSkillsEvent.contains(filter.getTitle()));
    }
}
