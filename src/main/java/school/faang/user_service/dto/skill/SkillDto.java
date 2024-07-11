package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {

    private Long id;

    @NotBlank
    @Size(min = 3, message = "Name must be have minimum 3 characters")
    private String title;
}
