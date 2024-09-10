package school.faang.user_service.EventOrganization.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SkillDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
