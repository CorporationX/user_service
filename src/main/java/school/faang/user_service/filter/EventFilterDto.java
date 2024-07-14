package school.faang.user_service.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private LocalDateTime startDateBeforePattern;
    private LocalDateTime startDateAfterPattern;
    private LocalDateTime endDateBeforePattern;
    private LocalDateTime endDateAfterPattern;
    private String locationPattern;
    private int maxAttendeesFromPattern;
    private int maxAttendeesToPattern;
    private User ownerPattern;
    private List<Skill> relatedSkillIsPattern;
    private boolean optionFilterSkills;
}
