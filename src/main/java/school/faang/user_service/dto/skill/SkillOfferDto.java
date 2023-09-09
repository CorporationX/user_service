package school.faang.user_service.dto.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillOfferDto {
    private Long id;
    @NotNull
    @Min(value = 1, message = "Please select skill")
    private Long skill;
    private Long recommendation;
    @NotNull
    private Long authorId;
    @NotNull
    @Min(value = 1, message = "Please select receiver")
    private Long receiverId;
}