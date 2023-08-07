package school.faang.user_service.dto.recommendation;

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
    private Long id;
    private String title;
    private List<UserSkillGuaranteeDto> guarantees;
}