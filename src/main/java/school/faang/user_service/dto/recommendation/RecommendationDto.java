package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecommendationDto {
    private Long id;
    @NotNull
    private Long authorId;
    @NotNull
    private Long receiverId;
    @NotBlank
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
