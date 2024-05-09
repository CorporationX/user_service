package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    Long id;
    String title;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Long ownerId;
    String description;
    List<SkillDto> relatedSkills;
    String location;
    int maxAttendees;
    List<UserDto> attendees;
}
