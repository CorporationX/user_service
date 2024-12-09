package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SkillDto {

    @NotNull
    private long id;

    @NotNull(message = "The skill cannot be null")
    @NotBlank(message = "The skill cannot be blank")
    private String title;
}
