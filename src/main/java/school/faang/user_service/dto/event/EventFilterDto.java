package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EventFilterDto {
    private Long id;
    private String title;
    private LocalDateTime laterThanStartDate;
    private LocalDateTime earlierThanEndDate;
    private Long ownerId;
    private String description;
    private List<SkillDto> relatedSkills;
    private String location;
    private int lessThanMaxAttendees;
}
