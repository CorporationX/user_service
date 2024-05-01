package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFilterDto {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private List<SkillDto> relatedSkillsFilter;
    private String locationFilter;
    private EventType typeFilter;
    private EventStatus statusFilter;

    public Predicate<Event> toPredicate() {
        List<Predicate<Event>> predicates = new ArrayList<>();

        if (fromDate != null) {
            predicates.add(event -> event.getStartDate().isAfter(fromDate));
        }
        if (toDate != null) {
            predicates.add(event -> event.getEndDate().isBefore(toDate));
        }
        if (relatedSkillsFilter != null && !relatedSkillsFilter.isEmpty()) {
            Set<Long> filterSkillIds = relatedSkillsFilter.stream().map(SkillDto::getId).collect(Collectors.toSet());
            predicates.add(event -> event.getRelatedSkills().stream()
                    .map(Skill::getId)
                    .collect(Collectors.toSet())
                    .containsAll(filterSkillIds)
            );
        }
        if (locationFilter != null) {
            predicates.add(event -> event.getLocation().equals(locationFilter));
        }
        if (typeFilter != null) {
            predicates.add(event -> event.getType().equals(typeFilter));
        }
        if (statusFilter != null) {
            predicates.add(event -> event.getStatus().equals(statusFilter));
        }

        if (predicates.isEmpty()) {
            return event -> true;
        }

        Predicate<Event> out = predicates.get(0);
        for (int i = 1; i < predicates.size(); i++) {
            out = out.and(predicates.get(i));
        }
        return out;
    }
}
