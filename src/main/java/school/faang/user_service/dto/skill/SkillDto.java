package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    private String title;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public List<Long> getUserIds () {
        return Optional.ofNullable(userIds).orElse(Collections.emptyList());
    }
}
