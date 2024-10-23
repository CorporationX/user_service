package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    private Long id;
    @NotNull(message = "Mustn't be null!")
    @Positive(message = "Author id should be more than 0!")
    private Long authorId;
    @NotNull(message = "Mustn't be null!")
    @Positive(message = "Receiver id should be more than 0!")
    private Long receiverId;
    private String content;
    @NotEmpty(message = "Skill offers list cannot be neither null nor empty!")
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
