package school.faang.user_service.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Data
@NoArgsConstructor
public class EventFilterDto {
    private final List<Predicate<Event>> filters = new ArrayList<>();

    private String title;
    private String description;
    private LocalDateTime startDateBefore;
    private LocalDateTime startDateAfter;
    private LocalDateTime endDateBefore;
    private LocalDateTime endDateAfter;
    private String location;
    private int maxAttendeesFrom;
    private int maxAttendeesTo;
    private User owner;
    private List<Long> relatedSkillIds;
    private boolean optionFilterSkills;

    public List<Predicate<Event>> getFilters() {
        if (notNull(title)) {
            filters.add(event -> event.getTitle().toLowerCase().contains(title.toLowerCase()));
        }
        if (notNull(description)) {
            filters.add(event -> event.getDescription().toLowerCase().contains(description.toLowerCase()));
        }
        if (notNull(startDateBefore)) {
            filters.add(event -> event.getStartDate().isBefore(startDateBefore));
        }
        if (notNull(startDateAfter)) {
            filters.add(event -> (event.getStartDate().isAfter(startDateAfter)));
        }
        if (notNull(endDateBefore)) {
            filters.add(event -> event.getEndDate().isBefore(endDateBefore));
        }
        if (notNull(endDateAfter)) {
            filters.add(event -> event.getEndDate().isAfter(endDateAfter));
        }
        if (notNull(location)) {
            filters.add(event -> event.getLocation().toLowerCase().contains(location.toLowerCase()));
        }
        if (maxAttendeesFrom > 0) {
            filters.add(event -> event.getMaxAttendees() >= maxAttendeesFrom);
        }
        if (maxAttendeesTo > 0) {
            filters.add(event -> event.getMaxAttendees() <= maxAttendeesTo);
        }
        if (notNull(owner)) {
            filters.add(event -> event.getOwner().equals(owner));
        }
        if (notNull(relatedSkillIds)) {
            if (optionFilterSkills) {
                filters.add(event -> event.getRelatedSkills().stream()
                        .map(Skill::getId)
                        .allMatch(skillId -> relatedSkillIds.contains(skillId))
                );
            } else {
                filters.add(event -> event.getRelatedSkills().stream()
                        .map(Skill::getId)
                        .anyMatch(skillId -> relatedSkillIds.contains(skillId))
                );
            }
        }

        return List.copyOf(filters);
    }

    private boolean notNull(Object field) {
        return field != null;
    }
}
