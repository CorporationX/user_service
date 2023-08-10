package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDto {
    @NotNull
    private Long id;
    private Long authorId;
    private Long receiverId;
    @NotNull
    @NotBlank
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}
