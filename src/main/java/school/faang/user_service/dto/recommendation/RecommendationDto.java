package school.faang.user_service.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RecommendationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Author id can't be null")
    private Long authorId;

    @NotNull(message = "Receiver id can't be null")
    private Long receiverId;

    @NotBlank(message = "Content shouldn't be empty")
    private String content;

    @Size(min = 1, message = "Skill offer shouldn't be empty")
    private List<SkillOfferDto> skillOffers;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
