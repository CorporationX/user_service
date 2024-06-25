package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    @NotNull
    private Long id;
    @NotNull
    @Size(min = 2, max = 100)
    private String title;
    @NotNull
    private List<Long> userIds;
}
