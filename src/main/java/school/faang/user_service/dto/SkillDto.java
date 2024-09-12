package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    @Positive(message = "ID must be positive")
    private Long id;

    @NotNull(message = "Skill title cannot be null.")
    private String title;
}
