package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {

    private Long id;

    @Min(0)
    @Max(300)
    private String title;

    private List<Long> userIds;
}
