package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    private String title;
    private List<Long> users;
    private List<UserSkillGuarantee> guarantees;
    private List<EventDto> events;
    private List<Long> goals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}