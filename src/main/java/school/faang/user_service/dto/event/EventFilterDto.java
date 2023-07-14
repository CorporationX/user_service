package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class EventFilterDto implements Predicate<EventDto> {
    private Long id;
    private String title;
    private LocalDateTime laterThanStartDate;
    private LocalDateTime earlierThanEndDate;
    private Long ownerId;
    private String description;
    private List<SkillDto> relatedSkills;
    private String location;
    private int lessThanMaxAttendees;

    /*Здесь проверяется полученный EventDto с созданным фильтром.*/
    @Override
    public boolean test(EventDto eventDto) {
        if (id != null && !id.equals(eventDto.getId())) {
            return false;
        }
        if (title != null && !title.equalsIgnoreCase(eventDto.getTitle())) {
            return false;
        }
        if (eventDto.getStartDate() != null && laterThanStartDate != null && !laterThanStartDate.isAfter(eventDto.getStartDate())) {
            return false;
        }
        if (eventDto.getEndDate() != null && earlierThanEndDate != null && !earlierThanEndDate.isBefore(eventDto.getEndDate())) {
            return false;
        }
        if (ownerId != null && !ownerId.equals(eventDto.getOwnerId())) {
            return false;
        }
        if (description != null && !description.equalsIgnoreCase(eventDto.getDescription())) {
            return false;
        }
        if (relatedSkills != null && !new HashSet<>(relatedSkills).containsAll(eventDto.getRelatedSkills())) {
            return false;
        }
        if (location != null && !location.equalsIgnoreCase(eventDto.getLocation())) {
            return false;
        }
        if (lessThanMaxAttendees != 0 && lessThanMaxAttendees < eventDto.getMaxAttendees()) {
            return false;
        }
        return true;
    }
}
