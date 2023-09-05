package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Author id is required")
    private Long authorId;
    @NotNull(message = "Receiver id is required")
    private Long receiverId;
    @NotBlank(message = "Content is required")
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
