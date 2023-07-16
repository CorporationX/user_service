package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SkillDto {
    private Long id;
    private String title;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
