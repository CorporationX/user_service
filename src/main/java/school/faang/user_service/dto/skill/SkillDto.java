package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class SkillDto {
    private Long id;
    @NotBlank(message = "Skill title cant be empty or null")
    private String title;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
