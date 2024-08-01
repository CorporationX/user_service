package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    @NotBlank(message = "Skill title cant be empty or null")
    private String title;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
