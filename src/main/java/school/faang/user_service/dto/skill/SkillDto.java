package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDto {
    @Min(value = 0, message = "Id should be a positive value")
    private Long id;
    @NotBlank(message = "Title cannot be blank")
    private String title;
}