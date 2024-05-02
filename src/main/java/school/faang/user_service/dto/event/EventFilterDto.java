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
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<SkillDto> relatedSkills;
    private String location;
    private EventType type;
    private EventStatus status;

    public Predicate<Event> toPredicate() {
        Predicate<Event> out = event -> true;
        out = out.and(getFromDateFilter());
        out = out.and(getToDateFilter());
        out = out.and(getRelatedSkillsFilter());
        out = out.and(getLocationFilter());
        out = out.and(getTypeFilter());
        out = out.and(getStatusFilter());
        return out;
    }

    public Predicate<Event> getFromDateFilter() {
        if (startDate != null) {
            return event -> event.getStartDate().isAfter(startDate);
        }
        return event -> true;
    }

    public Predicate<Event> getToDateFilter() {
        if (endDate != null) {
            return event -> event.getEndDate().isBefore(endDate);
        }
        return event -> true;
    }

    public Predicate<Event> getRelatedSkillsFilter() {
        if (relatedSkills != null && !relatedSkills.isEmpty()) {
            Set<Long> filterSkillIds = relatedSkills.stream().map(SkillDto::getId).collect(Collectors.toSet());
            return event -> event.getRelatedSkills().stream()
                    .map(Skill::getId)
                    .collect(Collectors.toSet())
                    .containsAll(filterSkillIds);
        }
        return event -> true;
    }

    public Predicate<Event> getLocationFilter() {
        if (location != null) {
            return event -> event.getLocation().equals(location);
        }
        return event -> true;
    }

    public Predicate<Event> getTypeFilter() {
        if (type != null) {
            return event -> event.getType().equals(type);
        }
        return event -> true;
    }

    public Predicate<Event> getStatusFilter() {
        if (status != null) {
            return event -> event.getStatus().equals(status);
        }
        return event -> true;
    }
}
