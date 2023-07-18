package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SkillDto {
    private Long id;
    private String title;
    private List<UserSkillGuaranteeDto> guarantees;
}